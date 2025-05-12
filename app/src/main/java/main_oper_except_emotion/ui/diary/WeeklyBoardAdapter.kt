package main_oper_except_emotion.ui.diary

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.main_oper_except_emotion.databinding.ItemWeeklyBoardBinding
import dagger.hilt.android.AndroidEntryPoint
import main_oper_except_emotion.requestandresponse.diary.WeekBoardCheckResponse

// 어뎁터는 데이터를 어떻게 보여줄지를 저의하고
// 프레그먼트에서는 그 어뎁터를 리사이클러뷰에 연결해서 실제로 화면에 띄운다
// 프레그먼트는 데이터를 받아서 어뎁터에 넘김
// 어뎁터는 데이터를 아이템 레이아웃에 바인딩함

class WeeklyBoardAdapter(
    private val items: List<WeekBoardCheckResponse>, // 서버에서 받아온 주간 일기 리스트
    private val onItemClick: (Int) -> Unit // 주간 일기 조회시, 전달 받은 dailyId 전달
) : RecyclerView.Adapter<WeeklyBoardAdapter.WeeklyBoardViewHolder>() {

    // ViewHolder 클래스
    inner class WeeklyBoardViewHolder(val binding: ItemWeeklyBoardBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeeklyBoardViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemWeeklyBoardBinding.inflate(inflater, parent, false)
        return WeeklyBoardViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WeeklyBoardViewHolder, position: Int) {
        val item = items[position]

        with(holder.binding) {
            // 날짜 표시 (같은 날짜는 숨김)
            if (position == 0 || item.date != items[position - 1].date) {
                tvDate.text = item.date
                tvDate.visibility = View.VISIBLE
            } else {
                tvDate.visibility = View.GONE
            }

            // 작성자 이름, 시간, 내용
            tvAuthor.text = item.name
            tvTime.text = item.time
            tvContent.text = item.content

            // 좋아요와 댓글 개수 표시
            val hasStats = item.like_count > 0 || item.comment_count > 0
            if (hasStats) {
                tvStats.text = buildString {
                    if (item.like_count > 0) append("좋아요 ${item.like_count}개   ")
                    if (item.comment_count > 0) append("댓글 ${item.comment_count}개")
                }
                tvStats.visibility = View.VISIBLE
            } else {
                tvStats.visibility = View.GONE
            }

            // 아이템 클릭 시, 해당 일기의 ID를 전달하여 상세보기 화면으로 이동
            root.setOnClickListener {
                onItemClick(item.daily_id) // 클릭 시 dailyId 전달
            }
        }
    }

    override fun getItemCount(): Int = items.size
}
