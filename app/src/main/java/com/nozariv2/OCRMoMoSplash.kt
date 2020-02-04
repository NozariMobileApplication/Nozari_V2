package com.nozariv2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.gridtester.MomoViewModel
import com.example.gridtester.Payer
import com.example.gridtester.RequestToPay
import java.util.*

class OCRMoMoSplash : AppCompatActivity() {

    var mainViewModel: MomoViewModel? = null

    val transactionUUID : String = UUID.randomUUID().toString()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ocrsplash)

        mainViewModel = ViewModelProviders.of(this).get(MomoViewModel::class.java)

        makeRequest(transactionUUID)

        //Add loader for processing the transaction, before being able to check it (also a nice way to manage dialogue with user
        
        

        val btn = findViewById<Button>(R.id.button4)

        btn.setVisibility(View.INVISIBLE)

        btn.setOnClickListener {

            checkRequest(transactionUUID)

            val intent = Intent(this, OCRTranslationSplash::class.java).apply {



                //this.setData(intent.data)
                //putExtra("language_selection", intent.getStringExtra("language_selection"))
            }
            startActivity(intent)
        }
    }

    fun makeRequest(uuid : String) {

        /*       mainViewModel!!.tokenResponse.observe(this, Observer {  response ->

                   Log.i("MA: Response code token", response.code().toString())

               })*/

        val payer = Payer("MSISDN", "46733123454")
        val req = RequestToPay("100", "EUR", "12345", payer ,"Payee Note", "Payer Message")

        mainViewModel!!.postRequestToPay("sandbox", uuid, req).observe(this, Observer { response ->

            Log.i("MA: Response code RTP", response.code().toString())
            val btn = findViewById<Button>(R.id.button4)
            btn.setVisibility(View.VISIBLE)

        })
    }

    fun checkRequest(uuid: String){

        mainViewModel!!.getRequestToPay("sandbox", uuid).observe(this, Observer { response ->

            Log.i("MA: Response code CR", response.code().toString())
            Log.i("MA: Response", response.body()!!.status)
            
            if(response.body()!!.status.equals("SUCCESSFUL")){

                upRequestSuccessful()

            } else if(response.body()!!.status.equals("PENDING")) {
                Log.i("MA: Response", response.body()!!.status)
                upRequestStillPending()

            } else if(response.body()!!.status.equals("FAILED")) {

                upRequestDeclined()

            }

        })

    }
    
    fun upRequestStillPending(){

        Toast.makeText(this@OCRMoMoSplash, "Still Pending", Toast.LENGTH_LONG).show()
        
    }
    
    fun upRequestDeclined(){

        Toast.makeText(this@OCRMoMoSplash, "Request Declined", Toast.LENGTH_LONG).show()

    }
    
    fun upRequestSuccessful(){

        Toast.makeText(this@OCRMoMoSplash, "Request Successful", Toast.LENGTH_LONG).show()

    }
    
    
    
    
    
    
}
