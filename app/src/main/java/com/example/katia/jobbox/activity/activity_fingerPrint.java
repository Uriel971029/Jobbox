package com.example.katia.jobbox.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.KeyguardManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.katia.jobbox.R;
import com.example.katia.jobbox.controller.FingerprintHandler;
import com.example.katia.jobbox.model.Auth.auth;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import static com.example.katia.jobbox.activity.MainActivity.FLAG_FINGERPRINT;


public class activity_fingerPrint extends AppCompatActivity {

    private KeyStore keyStore;
    // Variable utilizada para almacenar la llave que utilizaremos para la autenticación
    private static final String KEY_NAME = "pruebaHuella";
    private Cipher cipher;
    private TextView textView;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fingerprint);


        btnLogin = findViewById(R.id.btnLogin);


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //INICIAR SESIÓN CLÁSICAMENTE
                FLAG_FINGERPRINT = false;
                //Intent intent = new Intent(activity_fingerPrint.this, auth.class);
                //startActivity(intent);
                ((ResultReceiver) getIntent().getParcelableExtra("isFingerprintUsed")).send(2, new Bundle());
                finish();
            }
        });


        // Inicialización de elemetos managers para el proceso
        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        FingerprintManager fingerprintManager = null;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            fingerprintManager = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);

            textView = (TextView) findViewById(R.id.errorText);

            // Vericiar si el dispositivo tiene sensor
            if (!fingerprintManager.isHardwareDetected()) {

                //SI NO HAY SENSOR VOLVEMOS A LA CLASE AUTH
                FLAG_FINGERPRINT = false;
                ((ResultReceiver) getIntent().getParcelableExtra("isFingerprintUsed")).send(2, new Bundle());
                finish();

            } else {
                // VERIFICAR LOS PERMISOS DE LA HUELLA BIOMÉTRICA
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
                    textView.setText("La autenticación por huella esta deshabilitada");
                } else {
                    // VERIFICAR SI HAY HUELLAS REGISTRADAS EN EL DISPOSITIVO
                    if (!fingerprintManager.hasEnrolledFingerprints()) {
                        textView.setText("Por favor registre por lo menos una huella en su dispositivo");
                    } else {

                        if (!keyguardManager.isKeyguardSecure()) {
                            textView.setText("Huella con bloqueo de pantalla no habilitada");
                        } else {

                            //GENERAMOS UN LLAVE PARA EL PROCESO DE AUTENTICACIÓN
                            generateKey();
                            if (cipherInit()) {
                                FingerprintManager.CryptoObject cryptoObject = new FingerprintManager.CryptoObject(cipher);
                                FingerprintHandler helper = new FingerprintHandler(this);
                                helper.startAuth(fingerprintManager, cryptoObject);
                            }
                        }
                    }
                }
            }

        } else {
            //LA VERSIÓN DE ANDROID NO CUMPLE
            FLAG_FINGERPRINT = false;
            ((ResultReceiver) getIntent().getParcelableExtra("isFingerprintUsed")).send(2, new Bundle());
            finish();
        }

    }


    @TargetApi(Build.VERSION_CODES.M)
    protected void generateKey() {
        try {
            keyStore = KeyStore.getInstance("AndroidKeyStore");
        } catch (Exception e) {
            e.printStackTrace();
        }


        KeyGenerator keyGenerator;
        try {
            keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            throw new RuntimeException("Failed to get KeyGenerator instance", e);
        }


        try {
            keyStore.load(null);
            keyGenerator.init(new
                    KeyGenParameterSpec.Builder(KEY_NAME,
                    KeyProperties.PURPOSE_ENCRYPT |
                            KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(
                            KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .build());
            keyGenerator.generateKey();
        } catch (NoSuchAlgorithmException |
                InvalidAlgorithmParameterException
                | CertificateException | IOException e) {
            throw new RuntimeException(e);
        }
    }


    @TargetApi(Build.VERSION_CODES.M)
    public boolean cipherInit() {
        try {
            cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/" + KeyProperties.BLOCK_MODE_CBC + "/" + KeyProperties.ENCRYPTION_PADDING_PKCS7);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new RuntimeException("Failed to get Cipher", e);
        }


        try {
            keyStore.load(null);
            SecretKey key = (SecretKey) keyStore.getKey(KEY_NAME,
                    null);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return true;
        } catch (KeyPermanentlyInvalidatedException e) {
            return false;
        } catch (KeyStoreException | CertificateException | UnrecoverableKeyException | IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Failed to init Cipher", e);
        }
    }
}
