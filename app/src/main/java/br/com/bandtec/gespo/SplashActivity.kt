package br.com.bandtec.gespo

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.com.bandtec.gespo.model.Employee
import br.com.bandtec.gespo.requests.AuthRequest
import kotlinx.android.synthetic.main.activity_login.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SplashActivity : AppCompatActivity() {
    val valor = 1;

    var preferences: SharedPreferences? = null

    val api = Retrofit.Builder()
        .baseUrl("https://gespo-rest.azurewebsites.net/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val employeeRequests = api.create(AuthRequest::class.java)

    override fun onCreate(savedInstanceState: Bundle?){                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash)
        preferences = getSharedPreferences("Gespo", Context.MODE_PRIVATE)
        check();
    }

    private fun check(){
        Handler().postDelayed({
            val offlineId = preferences?.getInt("id", 0)
            if(offlineId === 0){
                val loginActivity = Intent(this, LoginActivity::class.java)
                startActivity(loginActivity)
            }else{
                val login = employeeRequests.login(preferences?.getString("cpf", "").toString(), preferences?.getString("pass", "").toString())

                val context = this

                login.enqueue(object : Callback<Employee> {
                    override fun onFailure(call: Call<Employee>, t: Throwable) {
                        Toast.makeText(applicationContext, "ERRO", Toast.LENGTH_SHORT).show()
                    }

                    override fun onResponse(call: Call<Employee>, response: Response<Employee>) {

                        val code = response.code()

                        when(code){
                            200 -> {
                                val mainActivity = Intent(context, MainActivity::class.java)
                                val cookie = response.headers().get("Set-Cookie")

                                val editor = preferences?.edit()
                                editor?.remove("cookie")
                                editor?.putString("cookie", cookie)
                                editor?.commit()

                                startActivity(mainActivity)
                            }
                            401 -> {
                                Toast.makeText(applicationContext, "Login e/ou senha inválido(s)", Toast.LENGTH_SHORT).show()
                                loading.visibility = View.GONE
                            }
                            403 -> {
                                Toast.makeText(applicationContext, "Login e/ou senha inválido(s)", Toast.LENGTH_SHORT).show()
                                loading.visibility = View.GONE
                            }
                        }

                    }
                })
            }
        }, 3000)
    }
}