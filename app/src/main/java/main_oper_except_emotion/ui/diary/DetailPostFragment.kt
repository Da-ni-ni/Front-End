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
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
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

@AndroidEntryPoint
class DetailPostFragment : Fragment() {

    private var _binding: FragmentDetailPostBinding? = null
    private val binding get() = _binding!!

    private val args: FragmentDetailPostBinding by navArgs()
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

        val dailyId = arguments?.getInt("dailyId") ?: return
        viewModel.getPostDetail(dailyId)

        binding.btnLike.setOnClickListener {
            viewModel.toggleLike(dailyId)
        }

        binding.btnPostComment.setOnClickListener {
            val commentText = binding.etCommentInput.text.toString().trim()
            if (commentText.isNotEmpty()) {
                val today = LocalDate.now().toString()
                val request = CreateCommentRequest(date = today, comment = commentText)
                viewModel.addComment(dailyId, request)
                binding.etCommentInput.setText("")
                Toast.makeText(requireContext(), "댓글이 등록되었습니다.", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnDeleteDiary.setOnClickListener {
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

        lifecycleScope.launchWhenStarted {
            viewModel.postDetail.collectLatest { detail ->
                detail?.let {
                    binding.tvAuthor.text = it.name
                    binding.tvTime.text = it.time
                    binding.tvContent.text = it.content

                    binding.commentContainer.removeAllViews()

                    it.comments.forEach { comment ->
                        val commentLayout = LinearLayout(requireContext()).apply {
                            orientation = LinearLayout.HORIZONTAL
                            setPadding(0, 8, 0, 8)
                        }

                        val tvComment = TextView(requireContext()).apply {
                            text = "- ${comment.name}: ${comment.comment}"
                            layoutParams = LinearLayout.LayoutParams(
                                0,
                                ViewGroup.LayoutParams.WRAP_CONTENT,
                                1f
                            )
                        }

                        val btnEdit = Button(requireContext()).apply {
                            text = "수정"
                            setOnClickListener {
                                showEditDialog(dailyId, comment.comment_id, comment.comment)
                            }
                        }

                        val btnDelete = Button(requireContext()).apply {
                            text = "삭제"
                            setOnClickListener {
                                AlertDialog.Builder(requireContext())
                                    .setTitle("댓글 삭제")
                                    .setMessage("정말 이 댓글을 삭제할까요?")
                                    .setPositiveButton("삭제") { _, _ ->
                                        viewModel.deleteComment(dailyId, comment.comment_id)
                                        Toast.makeText(
                                            requireContext(),
                                            "댓글 삭제됨",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                    .setNegativeButton("취소", null)
                                    .show()
                            }
                        }

                        commentLayout.addView(tvComment)
                        commentLayout.addView(btnEdit)
                        commentLayout.addView(btnDelete)

                        binding.commentContainer.addView(commentLayout)
                    }
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showEditDialog(dailyId: Int, commentId: Int, currentContent: String) {
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
                    val currentDate = LocalDate.now().toString()
                    val request = UpdateCommentRequest(date = currentDate, comment = newContent)
                    viewModel.updateComment(dailyId, commentId, request)
                    Toast.makeText(requireContext(), "수정됨", Toast.LENGTH_SHORT).show()
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