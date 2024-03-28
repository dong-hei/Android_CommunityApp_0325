package com.dk.mylivealonelife.contentList

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dk.mylivealonelife.R
import com.dk.mylivealonelife.utils.FirebaseAuthUtil
import com.dk.mylivealonelife.utils.FirebaseRefUtil

class ContentRVAdapter(val context: Context,
                       val items : ArrayList<ContentModel>,
                       val keyList : ArrayList<String>,
                       val bookmarkList : MutableList<String>) :
    RecyclerView.Adapter<ContentRVAdapter.Viewholder>() {

//        사진 클릭시 이동되는 코드
//    interface ItemClick {
//        fun onClick(view: View, position: Int)
//    }
//    var itemClick : ItemClick? = null

    //전체 아이템을 가져와서 레이아웃을 만들어준다.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContentRVAdapter.Viewholder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.content_rv_item, parent, false)
        return Viewholder(v)

    }

    override fun onBindViewHolder(holder: ContentRVAdapter.Viewholder, position: Int) {

//        사진 클릭시 이동되는 코드
//        if (itemClick != null) {
//            holder.itemView.setOnClickListener{ v-> itemClick?.onClick(v, position)}
//        }
        holder.bindItem(items[position], keyList[position])
    }

    //아이템의 갯수가 몇개인지
    override fun getItemCount(): Int {
        return items.size
    }

    inner class Viewholder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        
        //content_rv_item에 아이템에 데이터를 넣어준다
        fun bindItem(item: ContentModel, key: String) {

            // 사진 클릭시 이동되는 코드 2
            itemView.setOnClickListener{
//                Toast.makeText(context, item.title, Toast.LENGTH_SHORT).show()
                val intent = Intent(context, ContentShowActivity::class.java)
                intent.putExtra("url", item.webUrl)
                itemView.context.startActivity(intent)
            }

            val contentTitle = itemView.findViewById<TextView>(R.id.textArea)
            contentTitle.text = item.title

            val contentImg = itemView.findViewById<ImageView>(R.id.imageArea)
            val bookmarkArea = itemView.findViewById<ImageView>(R.id.bookmarkArea)

            if (bookmarkList.contains(key)) {
                bookmarkArea.setImageResource(R.drawable.bookmark_color)
            } else {
                bookmarkArea.setImageResource(R.drawable.bookmark_white)

            }

            bookmarkArea.setOnClickListener {
//                Log.d("ContentRVAdapter", FirebaseAuth.getUid()) uid 로그
                Toast.makeText(context, key , Toast.LENGTH_SHORT).show()

                //북마크가 있을때
                if (bookmarkList.contains(key)) {

//                    bookmarkList.remove(key) // 북마크가 있을때 다시 한번 클릭하면 키를 삭제

                    FirebaseRefUtil.bookmarkRef
                        .child(FirebaseAuthUtil.getUid())
                        .child(key)
                        .removeValue()
                }
                else {
                    //북마크가 없을 때, 북마크에 값을 바인딩
                    FirebaseRefUtil.bookmarkRef
                        .child(FirebaseAuthUtil.getUid())
                        .child(key)
                        .setValue(BookmarkModel(true))
                }
            }

            //Glide : 안드로이드에서 이미지 URL -> 화면 보여주는 라이브러리
            Glide.with(context)
                .load(item.imageUrl)
                .into(contentImg)
        }
    }
}