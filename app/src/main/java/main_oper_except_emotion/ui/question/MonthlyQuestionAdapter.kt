package main_oper_except_emotion.ui.question

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.main_oper_except_emotion.databinding.ItemMonthlyQuestionBinding
import main_oper_except_emotion.requestandresponse.question.MonthlyQuestion
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MonthlyQuestionAdapter(

    // onItemClick: (Int) -> Unit는 → "질문 클릭했을 때 question_id를 넘겨주는 콜백 함수"
    private val onItemClick: (Long) -> Unit
) : ListAdapter<MonthlyQuestion, MonthlyQuestionAdapter.QuestionViewHolder>(

    // DiffUtil은 새로운 리스트와 기존 리스트의 차이를 계산해서 RecyclerView를 효율적으로 갱신해줌
//    위 코드에서는 question_id가 같으면 같은 질문으로 판단
    object : DiffUtil.ItemCallback<MonthlyQuestion>() {
        override fun areItemsTheSame(oldItem: MonthlyQuestion, newItem: MonthlyQuestion) =
            oldItem.questionId == newItem.questionId

        override fun areContentsTheSame(oldItem: MonthlyQuestion, newItem: MonthlyQuestion) =
            oldItem == newItem
    }
) {
//    각 아이템(질문 하나)을 View로 표현할 때 사용하는 클래스
//    ViewBinding을 써서 XML의 TextView 등을 바로 접근 가능
    inner class QuestionViewHolder(private val binding: ItemMonthlyQuestionBinding) :
        RecyclerView.ViewHolder(binding.root) {
    @RequiresApi(Build.VERSION_CODES.O)
    fun bind(item: MonthlyQuestion) {
        val parsedDate = LocalDate.parse(item.date) // 예: "2025-05-01"
        val formatter = DateTimeFormatter.ofPattern("yyyy년 M월 d일")
        binding.tvDate.text = parsedDate.format(formatter)

        binding.tvQuestion.text = item.question
        binding.root.setOnClickListener {
            onItemClick(item.questionId)
        }
    }
}
//    질문 날짜와 내용 넣고
//    카드 전체를 눌렀을 때 question_id를 넘김

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemMonthlyQuestionBinding.inflate(inflater, parent, false)
        return QuestionViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: QuestionViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
