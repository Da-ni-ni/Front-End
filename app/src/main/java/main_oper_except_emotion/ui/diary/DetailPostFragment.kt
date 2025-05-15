package main_oper_except_emotion.ui.diary

import android.app.AlertDialog
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.main_oper_except_emotion.R
import com.example.main_oper_except_emotion.databinding.FragmentDetailPostBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import main_oper_except_emotion.requestandresponse.diary.CreateCommentRequest
import main_oper_except_emotion.requestandresponse.diary.UpdateCommentRequest
import main_oper_except_emotion.viewmodel.DiaryViewModel
import java.time.LocalDate
import kotlinx.coroutines.flow.collectLatest
import main_oper_except_emotion.OptionBottomSheetFragment
import main_oper_except_emotion.requestandresponse.diary.UpdateDiaryRequest
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale


@AndroidEntryPoint
class DetailPostFragment : Fragment() {

    private var _binding: FragmentDetailPostBinding? = null
    private val binding get() = _binding!!
    private val args: DetailPostFragmentArgs by navArgs()
    private val viewModel: DiaryViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailPostBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dailyId = when (args.source) {
            "write" -> args.dailyId
            else -> viewModel.selectedDailyId.value
        } ?: return

        viewModel.getPostDetail(dailyId)

        // ✅ 게시물 더보기 버튼 클릭
        binding.btnMoreDiary.setOnClickListener {
            val sheet = OptionBottomSheetFragment(
                type = OptionBottomSheetFragment.OptionType.POST,
                onEdit = { showUpdateDiaryDialog(dailyId) },
                onDelete = {
                    AlertDialog.Builder(requireContext())
                        .setTitle("일기를 삭제하시겠습니까?")
                        .setMessage("삭제된 일기는 복구할 수 없습니다.")
                        .setPositiveButton("삭제") { _, _ ->
                            viewModel.deleteDiary(dailyId)
                            Toast.makeText(requireContext(), "삭제되었습니다.", Toast.LENGTH_SHORT).show()
                            findNavController().popBackStack()
                        }
                        .setNegativeButton("취소", null)
                        .show()
                }
            )
            sheet.show(parentFragmentManager, "PostOptions")
        }

        viewModel.postDetail.observe(viewLifecycleOwner) { detail ->
            detail?.let {
                binding.tvAuthor.text = it.authorName
                val dateTime = LocalDateTime.parse(it.date)
                val formattedTime = dateTime.format(DateTimeFormatter.ofPattern("a h:mm", Locale.KOREA))
                binding.tvTime.text = formattedTime
                binding.tvContent.text = it.content
                binding.commentContainer.removeAllViews()

                it.comments.forEach { comment ->
                    val commentLayout = LinearLayout(requireContext()).apply {
                        orientation = LinearLayout.HORIZONTAL
                        setPadding(0, 8, 0, 8)
                    }

                    val tvComment = TextView(requireContext()).apply {
                        text = "- ${comment.authorName}: ${comment.content}"
                        layoutParams = LinearLayout.LayoutParams(
                            0,
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            1f
                        )
                    }

                    val btnMore = ImageButton(requireContext()).apply {
                        setImageResource(R.drawable.ic_more_vert)
                        background = ContextCompat.getDrawable(context, android.R.drawable.btn_default_small)
                        layoutParams = LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                        )
                        setOnClickListener {
                            val sheet = OptionBottomSheetFragment(
                                type = OptionBottomSheetFragment.OptionType.COMMENT,
                                onEdit = {
                                    showEditDialog(dailyId, comment.commentId, comment.content)
                                },
                                onDelete = {
                                    AlertDialog.Builder(requireContext())
                                        .setTitle("댓글 삭제")
                                        .setMessage("정말 이 댓글을 삭제할까요?")
                                        .setPositiveButton("삭제") { _, _ ->
                                            viewModel.deleteComment(dailyId, comment.commentId)
                                            viewModel.getPostDetail(dailyId)
                                            Toast.makeText(requireContext(), "댓글 삭제됨", Toast.LENGTH_SHORT).show()
                                        }
                                        .setNegativeButton("취소", null)
                                        .show()
                                }
                            )
                            sheet.show(parentFragmentManager, "CommentOptions")
                        }
                    }

                    commentLayout.addView(tvComment)
                    commentLayout.addView(btnMore)

                    binding.commentContainer.addView(commentLayout)
                }
            }
        }

        binding.btnPostComment.setOnClickListener {
            val content = binding.etCommentInput.text.toString().trim()
            if (content.isNotEmpty()) {
                val request = CreateCommentRequest(content)
                viewModel.addComment(dailyId, request)
                binding.etCommentInput.text.clear()
            }
        }
    }

    private fun showEditDialog(dailyId: Long, commentId: Long, currentContent: String) {
        val input = EditText(requireContext()).apply {
            setText(currentContent)
            setSelection(currentContent.length)
        }

        AlertDialog.Builder(requireContext())
            .setTitle("댓글 수정")
            .setView(input)
            .setPositiveButton("확인") { _, _ ->
                val newContent = input.text.toString().trim()
                if (newContent.isNotEmpty()) {
                    val request = UpdateCommentRequest(newContent)
                    viewModel.updateComment(dailyId, commentId, request)
                    viewModel.getPostDetail(dailyId)
                    Toast.makeText(requireContext(), "수정됨", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("취소", null)
            .show()
    }

    private fun showUpdateDiaryDialog(dailyId: Long) {
        val input = EditText(requireContext()).apply {
            hint = "새로운 일기 내용을 입력하세요"
        }

        AlertDialog.Builder(requireContext())
            .setTitle("일기 수정")
            .setView(input)
            .setPositiveButton("수정") { _, _ ->
                val newContent = input.text.toString().trim()
                if (newContent.isNotEmpty()) {
                    val request = UpdateDiaryRequest(newContent)
                    viewModel.updateDiary(dailyId, request)
                    viewModel.getPostDetail(dailyId)
                    Toast.makeText(requireContext(), "일기 수정 완료", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("취소", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
