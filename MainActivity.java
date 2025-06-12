package vn.tlu.edu.pxp.bt1;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    EditText edtName, edtPass;
    Button loginButton;

    DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edtName = findViewById(R.id.edtName);
        edtPass = findViewById(R.id.edtPass);
        loginButton = findViewById(R.id.loginButton);

        usersRef = FirebaseDatabase.getInstance().getReference("users");

        loginButton.setOnClickListener(v -> login());
    }

    private void login() {
        String inputName = edtName.getText().toString().trim();
        String inputPassword = edtPass.getText().toString().trim();

        if (inputName.isEmpty() || inputPassword.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean found = false;
                for (DataSnapshot userSnap : snapshot.getChildren()) {
                    String username = userSnap.child("username").getValue(String.class);
                    String password = userSnap.child("password").getValue(String.class);
                    String role = userSnap.child("role").getValue(String.class);

                    if (username != null && password != null && username.equals(inputName) && password.equals(inputPassword)) {
                        found = true;
                        Toast.makeText(MainActivity.this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();

                        // Mở màn hình tương ứng theo vai trò
                        if (role.equalsIgnoreCase("admin")) {
                            Intent intent = new Intent(MainActivity.this, AdminActivity.class);
                            intent.putExtra("name", username); // truyền email để lọc lịch
                            startActivity(intent);
                        } else if (role.equalsIgnoreCase("staff1")) {
                            Intent intent = new Intent(MainActivity.this, EmployeeActivity.class);
                            intent.putExtra("name", username);
                            startActivity(intent);
                        }

                        finish(); // đóng login
                        break;
                    }
                }

                if (!found) {
                    Toast.makeText(MainActivity.this, "Username hoặc mật khẩu không đúng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Lỗi kết nối Firebase", Toast.LENGTH_SHORT).show();
            }
        });
    }
}