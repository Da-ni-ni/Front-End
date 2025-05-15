package main_oper_except_emotion.ui.emotion

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.main_oper_except_emotion.R
import com.example.main_oper_except_emotion.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint

import main_oper_except_emotion.TokenManager
import main_oper_except_emotion.requestandresponse.emotion.PersonalEmotionDetailResponse
import main_oper_except_emotion.requestandresponse.emotion.getImageRes
import main_oper_except_emotion.viewmodel.EmotionViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    @Inject lateinit var tokenManager: TokenManager
    private val viewModel: EmotionViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 가족 전체의 감정을 로드
        viewModel.loadFamilyEmotions()

        // 가족명 수정
        binding.tvTitle.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_groupNameFragment)
        }

        // 나 (person1)
        // 뷰모델에 저장된 내 감정의 상태 변화를 관찰
        // 변화에 따라
        viewModel.myEmotion.observe(viewLifecycleOwner) { emotion ->
            val myName = tokenManager.getUserName()?.takeIf { it.isNotBlank() } ?: "나"

            val iv = binding.person1.findViewById<ImageView>(R.id.ivEmotionSticker1)
            val tv = binding.person1.findViewById<TextView>(R.id.tvPersonName1)


            // setImageResoure는 감정을 한 파일에서 관리하기 위해 만듦
            iv.setImageResource(
                if (emotion != null) getImageRes(emotion) else R.drawable.ic_default_emotion
            )
            tv.text = myName

            // 내 감정을 누르면 수정 페이지
            binding.person1.setOnClickListener {
                findNavController().navigate(R.id.action_homeFragment_to_myEmotionChangeFragment)
            }
        }

        // 가족들 (person2~10)
        val personViews = listOf(
            binding.person2,
            binding.person3,
            binding.person4,
            binding.person5,
            binding.person6,
            binding.person7,
            binding.person8,
            binding.person9,
            binding.person10,
        )

        // 가족들의 감정 변화 확인
        viewModel.familyEmotions.observe(viewLifecycleOwner) { familyList ->
            val myEmotionId = tokenManager.getEmotionId()

            // 기존 뷰 초기화
            // forEachIndexed는 Kotlin에서 리스트 등의 컬렉션을 인덱스와 함께 순회할 수 있는 확장 함수입니다.
            personViews.forEachIndexed { index, person ->
                val iv = person.findViewById<ImageView>(
                    resources.getIdentifier("ivEmotionSticker${index + 2}", "id", requireContext().packageName)
                )
                val tv = person.findViewById<TextView>(
                    resources.getIdentifier("tvPersonName${index + 2}", "id", requireContext().packageName)
                )

                iv.setImageResource(R.drawable.ic_default_emotion)
                tv.text = "-"
                person.setOnClickListener(null)
            }

            // 나를 제외한 가족 감정만 필터링
            val filtered = if (myEmotionId != null) {
                familyList.filter { it.emotionId != myEmotionId }
            } else {
                familyList
            }

            // 뷰에 반영
            filtered.forEachIndexed { index, member ->
                if (index < personViews.size) {
                    val person = personViews[index]
                    val iv = person.findViewById<ImageView>(
                        resources.getIdentifier("ivEmotionSticker${index + 2}", "id", requireContext().packageName)
                    )
                    val tv = person.findViewById<TextView>(
                        resources.getIdentifier("tvPersonName${index + 2}", "id", requireContext().packageName)
                    )

                    iv.setImageResource(getImageRes(member.emotion))
                    tv.text = member.nickname?.takeIf { it.isNotBlank() } ?: member.name  // ✅ 닉네임 우선 표시

                    person.setOnClickListener {
                        viewModel.fetchPersonalEmotion(member.emotionId)

                        val observer = object : Observer<PersonalEmotionDetailResponse?> {
                            override fun onChanged(detail: PersonalEmotionDetailResponse?) {
                                viewModel.personalEmotionDetail.removeObserver(this)

                                if (detail?.emotionId == member.emotionId) {
                                    val bundle = bundleOf(
                                        "emotionId" to member.emotionId.toString(),
                                        "userName" to (member.nickname?.takeIf { it.isNotBlank() } ?: member.name)
                                    )
                                    findNavController().navigate(
                                        R.id.action_homeFragment_to_personalFamilyNameFragment,
                                        bundle
                                    )
                                } else {
                                    Toast.makeText(requireContext(), "감정 조회에 실패했습니다.", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }

                        viewModel.personalEmotionDetail.observe(viewLifecycleOwner, observer)
                    }

                }
            }

            // 마지막 업데이트 시간 표시
            val latestTime = filtered
                .maxByOrNull { it.updatedAt }
                ?.updatedAt
                ?.let { formatToDisplay(it) }

            binding.tvLastUpdated.text = latestTime?.let { "$it 기준 갱신" }
                ?: "아직 감정을 등록한 가족이 없습니다"
        }


        viewModel.checkEmotionPopupNeeded()
        viewModel.showEmotionPopup.observe(viewLifecycleOwner) { shouldShow ->
            if (shouldShow) {
                EmotionPopupDialog().show(parentFragmentManager, "EmotionPopup")
            }
        }

        viewModel.familyName.observe(viewLifecycleOwner) { serverName ->
            if (serverName != null) {
                binding.tvTitle.text = "${serverName.ifBlank { "우리 가족" }}네 가족, 오늘의 감정"
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val localName = tokenManager.getFamilyName()?.takeIf { it.isNotBlank() } ?: "우리 가족"
        binding.tvTitle.text = "$localName, 네 가족, 오늘의 감정"

        val groupId = tokenManager.getGroupId()
        viewModel.updateFamilyGroupName(localName)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun formatToDisplay(updatedAt: String): String {
        return try {
            val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
            val outputFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm")
            val time = LocalDateTime.parse(updatedAt, inputFormatter)
            outputFormatter.format(time)
        } catch (e: Exception) {
            "-"
        }
    }
}