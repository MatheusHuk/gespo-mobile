package br.com.bandtec.gespo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import br.com.bandtec.gespo.model.Employee
import br.com.bandtec.gespo.requests.AuthRequest
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_login.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LoginActivity : AppCompatActivity() {

    val api = Retrofit.Builder()
        .baseUrl("https://gespo-rest.azurewebsites.net/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val employeeRequests = api.create(AuthRequest::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        Glide.with(this)
            .load(R.mipmap.ring) // aqui é teu gif
            .asGif()
            .into(loadingImage);
    }

    fun login(v: View){

        loading.visibility = View.VISIBLE
        val cpf = et_cpf.text.toString()
        val pass = et_senha.text.toString()
        
        val login = employeeRequests.login(cpf, pass)
        
        login.enqueue(object : Callback<Employee> {
            override fun onFailure(call: Call<Employee>, t: Throwable) {
                Toast.makeText(applicationContext, "ERRO", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<Employee>, response: Response<Employee>) {

                val code = response.code()
                
                when(code){
                    200 -> {
                        val mainActivity = Intent(applicationContext, MainActivity::class.java)
                        mainActivity.putExtra("username", response.body()?.name.toString())
                        mainActivity.putExtra("id", response.body()?.id)
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
}