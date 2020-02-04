package com.nozariv2

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.UiThread
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.io.File
import java.io.IOException
import java.lang.Runnable
import java.text.SimpleDateFormat
import java.util.*

class MainActivityOld : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        val textView = findViewById<TextView>(R.id.text)

        //TRANSLATION THINGS
        GlobalScope.launch{ makeTranslationRequest() }

/*        Thread(Runnable {
            val out = trans.callUrlAndParseResult("en", "zu", "hello how are you")
            Log.i("info", out)
        }).start()


        GlobalScope.launch {
            (trans.callUrlAndParseResult("en", "zu", "hello how are you"))
        }*/

        //textView.text = trans.callUrlAndParseResult("en", "zu", "hello how are you")

    }



    private val PERMISSION_CODE = 1000
    private val IMAGE_CAPTURE_CODE = 1001
    var image_uri: Uri? = null

    fun openOCRCameraActivity(view: View){
            //if system os is Marshmallow or Above, we need to request runtime permission
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                if (checkSelfPermission(Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_DENIED ||
                    checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_DENIED ||
                    checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_DENIED){
                    //permission was not enabled
                    val permission = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                    //show popup to request permission
                    requestPermissions(permission, PERMISSION_CODE)
                }
                else{
                    //permission already granted
                    //openCamera()
                }
            }
            else{
                //system os is < marshmallow
                //openCamera()
            }
    }

    //TRANSLATION THINGS
    suspend fun makeTranslationRequest(){
        val result = slowFetch()
        val textView = findViewById<TextView>(R.id.text)
        withContext(Dispatchers.Main){
            textView.text = result
        }
    }

    fun slowFetch(): String{
        val trans = Translator()
        Log.i("info", "In slow fetch!")
        Log.i("info", trans.callUrlAndParseResult("en", "zu", "hello how are you"))
        return trans.callUrlAndParseResult("en", "zu", "hello how are you")
    }




/*    fun makeRequest(view: View){

        val textView = findViewById<TextView>(R.id.text)
// ...

// Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(this)
        val url = "http://www.google.com"

// Request a string response from the provided URL.
        val stringRequest = StringRequest(Request.Method.GET, url,
            Response.Listener<String> { response ->
                // Display the first 500 characters of the response string.
                textView.text = "Response is: ${response.substring(0, 500)}"
            },
            Response.ErrorListener { textView.text = "That didn't work!" })


    val URL = "http://...";
    val jsonBody: JSONObject = JSONObject()
    jsonBody.put("Title", "Android Volley Demo");
    jsonBody.put("Author", "BNK");
    val js = jsonBody.toString();





    // Add the request to the RequestQueue.
            queue.add(stringRequest)

    }

    fun makePostRequest(view: View){


        val queue = Volley.newRequestQueue(this)
        val textView = findViewById<TextView>(R.id.text)

        val url = "https://postman-echo.com/post"
        textView.text = ""

        // Post parameters
        // Form fields and values
        val params = HashMap<String,String>()
        params["foo1"] = "bar1"
        params["foo2"] = "bar2"
        val jsonObject = JSONObject(params as Map<*, *>)

        // Volley post request with parameters
        val request = JsonObjectRequest(Request.Method.POST,url,jsonObject,
            Response.Listener { response ->
                // Process the json
                try {
                    textView.text = "Response: $response"
                }catch (e:Exception){
                    textView.text = "Exception: $e"
                }

            }, Response.ErrorListener{
                // Error in request
                textView.text = "Volley error: $it"
            })


        // Volley request policy, only one time request to avoid duplicate transaction
        request.retryPolicy = DefaultRetryPolicy(
            DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
            // 0 means no retry
            0, // DefaultRetryPolicy.DEFAULT_MAX_RETRIES = 2
            1f // DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )

        // Add the volley post request to the request queue
        queue.add(request)
    }*/




}