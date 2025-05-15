package main_oper_except_emotion

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.example.main_oper_except_emotion.databinding.FragmentBottomSheetDialogBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class OptionBottomSheetFragment(
    private val type: OptionType,
    private val onEdit: () -> Unit,
    private val onDelete: () -> Unit,
    private val onReport: (() -> Unit)? = null
) : BottomSheetDialogFragment() {

    enum class OptionType {
        POST, COMMENT
    }

    private var _binding: FragmentBottomSheetDialogBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBottomSheetDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val container = binding.root // LinearLayout

        // 수정 버튼 (이미 XML에 있음)
        binding.btnEdit.setOnClickListener {
            onEdit()
            dismiss()
        }

        // 삭제 버튼 (이미 XML에 있음)
        binding.btnDelete.setOnClickListener {
            onDelete()
            dismiss()
        }

        // 댓글일 경우 "신고" 버튼을 동적으로 추가
        if (type == OptionType.COMMENT && onReport != null) {
            val report = createButton("신고") {
                onReport.invoke()
                dismiss()
            }
            container.addView(report)
        }
    }

    private fun createButton(text: String, onClick: () -> Unit): TextView {
        return TextView(requireContext()).apply {
            this.text = text
            textSize = 16f
            setPadding(32, 48, 32, 48)
            setBackgroundResource(android.R.drawable.list_selector_background)
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            setOnClickListener { onClick() }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
