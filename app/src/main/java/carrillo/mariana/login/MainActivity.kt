package carrillo.mariana.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import carrillo.mariana.login.ui.theme.LoginTheme
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.database

class MainActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        auth = Firebase.auth

       if(auth.currentUser != null){
           val intent = Intent(this, PrincipalActivity::class.java)
           startActivity(intent)
       }

        setContent {
            LoginTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    PantallaInicio(
                        auth,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun PantallaInicio(auth:FirebaseAuth, modifier: Modifier = Modifier) {

    var correo by remember {
        mutableStateOf("")
    }

    var contra by remember {
        mutableStateOf("")
    }

    val context = LocalContext.current

    Column(
        modifier = modifier.fillMaxSize().padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Text(text =  "Inicio de Sesión", fontSize =  24.sp)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = correo,
            onValueChange = { correo = it },
            label = { Text(text = "Correo Electrónico")},
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = contra,
            onValueChange = { contra = it },
            label = { Text(text = "Contraseña")},
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(){
            Button(onClick = {
                val intent = Intent(context, RegistroActivity::class.java)
                context.startActivity(intent)
            }) { Text(text = "Registrarse")}
            Spacer(modifier = Modifier.width(16.dp))
            Button(onClick = {
                if(contra.isNotEmpty() && correo.isNotEmpty()){
                    auth.signInWithEmailAndPassword(correo, contra)
                        .addOnCompleteListener { task ->
                            if(task.isSuccessful){
                                val intent = Intent(context, PrincipalActivity::class.java)
                                intent.putExtra("correo", correo)
                                context.startActivity(intent)
                            }else{
                                Toast.makeText(context, "Datos incorrectos", Toast.LENGTH_SHORT).show()
                            }
                        }
                }
            }) { Text(text = "Ingresar")}
        }
        Spacer(modifier = Modifier.height(16.dp))
        TextButton(onClick = {
            val intent = Intent(context, ContrasenaActivity::class.java)
            context.startActivity(intent)
        }){
            Text(text = "¿Olvidaste tu contraseña?")
        }

    }

}