package main_oper_except_emotion.ui.diary

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.main_oper_except_emotion.databinding.ItemWeeklyBoardBinding
import dagger.hilt.android.AndroidEntryPoint
import main_oper_except_emotion.WeekBoardUiModel
import main_oper_except_emotion.requestandresponse.diary.WeekBoardCheckResponse

// 어뎁터는 데이터를 어떻게 보여줄지를 저의하고
// 프레그먼트에서는 그 어뎁터를 리사이클러뷰에 연결해서 실제로 화면에 띄운다
// 프레그먼트는 데이터를 받아서 어뎁터에 넘김
// 어뎁터는 데이터를 아이템 레이아웃에 바인딩함

class WeeklyBoardAdapter(
    private val items: List<WeekBoardUiModel>,
    private val onItemClick: (WeekBoardUiModel) -> Unit
) : RecyclerView.Adapter<WeeklyBoardAdapter.WeeklyBoardViewHolder>() {

    inner class WeeklyBoardViewHolder(val binding: ItemWeeklyBoardBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeeklyBoardViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemWeeklyBoardBinding.inflate(inflater, parent, false)
        return WeeklyBoardViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WeeklyBoardViewHolder, position: Int) {
        val item = items[position]

        with(holder.binding) {
            val isFirst = position == 0
            val isDifferentDate = isFirst || item.date != items[position - 1].date

            // 날짜 or 시간 표시
            if (isDifferentDate) {
                // 날짜 구간 시작 시 → 날짜 표시
                tvDate.text = item.date
            } else {
                // 같은 날짜 안의 후속 글 → 시간만 표시
                tvDate.text = item.time
            }
            tvDate.visibility = View.VISIBLE

            tvAuthor.text = item.authorName
            tvContent.text = item.content

            val hasStats = item.likeCount > 0 || item.commentCount > 0
            if (hasStats) {
                tvStats.text = buildString {
                    if (item.likeCount > 0) append("좋아요 ${item.likeCount}개   ")
                    if (item.commentCount > 0) append("댓글 ${item.commentCount}개")
                }
                tvStats.visibility = View.VISIBLE
            } else {
                tvStats.visibility = View.GONE
            }

            root.setOnClickListener {
                onItemClick(item)
            }
        }
    }

    override fun getItemCount(): Int = items.size
}