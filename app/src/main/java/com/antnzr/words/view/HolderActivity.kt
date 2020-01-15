package com.antnzr.words.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.antnzr.words.R

class HolderActivity : AppCompatActivity() {

    private val fragmentManager = supportFragmentManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_holder)

        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment, WordListFragment.newInstance())
        fragmentTransaction.commit()
    }
}
