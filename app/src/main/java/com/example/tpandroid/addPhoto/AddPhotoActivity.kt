package com.example.tpandroid.addPhoto

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.tpandroid.R
import com.google.android.material.textfield.TextInputEditText

const val AUTHOR_NAME = "John Doe"
const val PHOTO_DESCRIPTION = "description"

class AddPhotoActivity : AppCompatActivity() {
    private lateinit var addPhotoName: TextInputEditText
    private lateinit var addPhotoDescription: TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_photo_layout)

        findViewById<Button>(R.id.done_button).setOnClickListener {
            addFlower()
        }
        addPhotoName = findViewById(R.id.add_flower_name)
        addPhotoDescription = findViewById(R.id.add_flower_description)
    }

    /* The onClick action for the done button. Closes the activity and returns the new photo name
    and description as part of the intent. If the name or description are missing, the result is set
    to cancelled. */

    private fun addFlower() {
        val resultIntent = Intent()

        if (addPhotoName.text.isNullOrEmpty() || addPhotoDescription.text.isNullOrEmpty()) {
            setResult(Activity.RESULT_CANCELED, resultIntent)
        } else {
            //Db operation

            val name = addPhotoName.text.toString()
            val description = addPhotoDescription.text.toString()

            resultIntent.putExtra(AUTHOR_NAME, name)
            resultIntent.putExtra(PHOTO_DESCRIPTION, description)
            setResult(Activity.RESULT_OK, resultIntent)
        }
        finish()
    }
}