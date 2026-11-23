package com.kaflix.app

import android.app.Activity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.Gravity
import android.view.View
import android.widget.*
import android.graphics.Color
import android.graphics.Typeface
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.net.URLEncoder
import java.util.Locale
import kotlin.concurrent.thread

class MainActivity : Activity(), TextToSpeech.OnInitListener {
    private val bg = Color.rgb(12,12,12); private val red = Color.rgb(229,9,20); private val white = Color.WHITE
    private lateinit var root: LinearLayout
    private var tts: TextToSpeech? = null
    private val http = OkHttpClient()
    private var username = ""; private var password = ""
    override fun onCreate(savedInstanceState: Bundle?) { super.onCreate(savedInstanceState); window.statusBarColor=bg; window.navigationBarColor=bg; tts=TextToSpeech(this,this); showSplash() }
    override fun onInit(status: Int) { if(status==TextToSpeech.SUCCESS) { tts?.language=Locale("pt","BR"); tts?.setSpeechRate(0.78f) } }
    private fun base(): String = BuildConfig.PROVIDER_BASE_URL.trimEnd('/')
    private fun page(): LinearLayout { root=LinearLayout(this).apply { orientation=LinearLayout.VERTICAL; setPadding(28,32,28,24); setBackgroundColor(bg) }; setContentView(root); return root }
    private fun label(s:String,size:Float=18f): TextView = TextView(this).apply { text=s; textSize=size; setTextColor(white); gravity=Gravity.CENTER; setPadding(8,12,8,12) }
    private fun button(s:String, action:()->Unit): Button = Button(this).apply { text=s; setTextColor(white); setBackgroundTintList(android.content.res.ColorStateList.valueOf(red)); setOnClickListener{action()} }
    private fun showSplash() { val p=page(); p.gravity=Gravity.CENTER; p.addView(label("KaFlix",42f).apply{setTypeface(null,Typeface.BOLD)}); p.addView(label("Seu entretenimento, em um só lugar",14f)); root.postDelayed({ tts?.speak("KaFlix",TextToSpeech.QUEUE_FLUSH,null,"kaflix"); showLogin() },900) }
    private fun showLogin() { val p=page(); p.gravity=Gravity.CENTER; p.addView(label("KaFlix",34f).apply{setTextColor(red);setTypeface(null,Typeface.BOLD)}); p.addView(label("Entre com seu usuário e senha",16f))
        val user=EditText(this).apply{hint="Usuário";setSingleLine();setTextColor(white);setHintTextColor(0xffaaaaaa.toInt())}; val pass=EditText(this).apply{hint="Senha";inputType= android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD;setSingleLine();setTextColor(white);setHintTextColor(0xffaaaaaa.toInt())}
        p.addView(user);p.addView(pass);p.addView(button("ENTRAR"){username=user.text.toString().trim();password=pass.text.toString(); login()}); p.addView(label("A conexão usa o servidor configurado no aplicativo.",12f))
    }
    private fun login() { if(base().contains("SEU-SERVIDOR-AQUI")){toast("Falta configurar o endereço do provedor no projeto antes de compilar.");return}; toast("Conectando..."); thread { try { val url="${base()}/player_api.php?username=${enc(username)}&password=${enc(password)}"; val req=Request.Builder().url(url).build(); http.newCall(req).execute().use { res -> val body=res.body?.string().orEmpty(); val info=JSONObject(body).optJSONObject("user_info"); val ok=res.isSuccessful && info?.optString("auth")=="1"; runOnUiThread { if(ok) showHome() else toast("Usuário, senha ou servidor inválido.") } } } catch(e:Exception){runOnUiThread{toast("Não foi possível conectar. Confira o servidor e a internet.")}} } }
    private fun showHome(){ val p=page(); p.addView(label("KaFlix",30f).apply{setTextColor(red);setTypeface(null,Typeface.BOLD)}); val tabs=LinearLayout(this).apply{orientation=LinearLayout.HORIZONTAL}; listOf("TV AO VIVO","FILMES","SÉRIES").forEach { name -> tabs.addView(button(name){loadSection(name) },LinearLayout.LayoutParams(0,-2,1f)) }; p.addView(tabs); p.addView(label("Escolha uma categoria para carregar seu catálogo.",15f)) }
    private fun loadSection(section:String){ toast("Carregando $section..."); val action=when(section){"TV AO VIVO"->"get_live_categories";"FILMES"->"get_vod_categories";else->"get_series_categories"}; thread { try { val url="${base()}/player_api.php?username=${enc(username)}&password=${enc(password)}&action=$action"; http.newCall(Request.Builder().url(url).build()).execute().use { r -> val txt=r.body?.string().orEmpty(); runOnUiThread { val p=page(); p.addView(label("$section",26f)); if(r.isSuccessful) { val arr=org.json.JSONArray(txt); for(i in 0 until minOf(arr.length(),30)){ val item=arr.optJSONObject(i); p.addView(label(item?.optString("category_name","Categoria")?:"Categoria",16f)) } } else p.addView(label("Falha ao carregar catálogo.")) } } }catch(e:Exception){runOnUiThread{toast("Erro ao carregar catálogo.")}} } }
    private fun enc(s:String)=URLEncoder.encode(s,"UTF-8")
    private fun toast(s:String)=Toast.makeText(this,s,Toast.LENGTH_LONG).show()
    override fun onDestroy(){tts?.stop();tts?.shutdown();super.onDestroy()}
}
