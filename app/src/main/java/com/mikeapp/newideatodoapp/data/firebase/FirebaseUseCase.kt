package com.mikeapp.newideatodoapp.data.firebase

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

object FirebaseUseCase {
    private var database: FirebaseDatabase = FirebaseDatabase.getInstance()

    fun firebase4() {
        val dbRef = database.reference
//        val messageRef: DatabaseReference = dbRef.child("message")
        val myRef = database.getReference("message")
        myRef.get().addOnCompleteListener {
            Log.d("bbbb", "firebase4: ${it.result?.value}")
        }
    }

    fun firebase3() {
        val dbRef = database.reference
        dbRef.child("users").child("user1").child("message")
            .addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        Log.d("bbbb", "onDataChange: ${snapshot.value}")
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.d("bbbb", "onCancelled: ${error.message}")
                    }
                }
            )

    }

    fun firebase2() {
        val myRef = database.getReference("users/user1/message") // Replace with your actual path

        // Attach a listener to read data
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                val value = dataSnapshot.getValue(String::class.java)
                if (value != null) {
                    // Do something with the retrieved string
                    Log.d("bbbb", "Value: $value")
                    // ... your logic to handle the string "haha 11=25"
                } else {
                    Log.w("bbbb", "Value is null")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w("bbbb", "Failed to read value.", error.toException())
            }
        })
    }

    fun firebase() {
        Log.d("bbbb", "database $database")
        // Reference to your database
        val myRef = database.getReference("users/user1/message")
        Log.d("bbbb", "myRef $myRef")
        // Write data
//        val message = HashMap<String, Any>()
//        message["text"] = "Hello, Firebase!"
//        myRef.push().setValue(message).addOnCompleteListener { task ->
//            if (task.isSuccessful) {
//                Log.d("bbbb", "Data written successfully")
//            } else {
//                Log.e("bbbb", "Error writing data", task.exception)
//            }
//        }
        Log.d("bbbb", "mid")
        myRef.get().addOnCompleteListener { task ->
            Log.d("bbbb", "Data read completed, task: $task")
            if (task.isSuccessful) {
                // Data successfully retrieved
                val value = task.result?.value
                Log.d("bbbb", "Data read from Firebase: $value")
            } else {
                // Handle errors
                Log.d("bbbb", "Error getting data: ${task.exception?.message}")
            }
        }
    }
}