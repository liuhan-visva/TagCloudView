package com.biubiustudio.tabcloud

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.biubiustudio.widget.TagsAdapter
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val data = MutableList(50) {
            it.toString()
        }

        sv.setAdapter(object: TagsAdapter(){
            override fun getCount(): Int {
                return data.size
            }

            override fun getItem(position: Int): Any? {
                return data[position]
            }

            override fun getPopularity(position: Int): Int {
                return 1
            }

            override fun getView(context: Context, position: Int, parent: ViewGroup): View {
                return TextView(context).apply { text = position.toString() }
            }

            override fun onThemeColorChanged(view: View, progress: Int) {
            }
        })
    }
}
