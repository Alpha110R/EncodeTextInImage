package com.guy.class23a_ands_2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class MainActivity extends AppCompatActivity {
    private static final int pswdIterations = 10;
    private static final int keySize = 128;
    private static final String cypherInstance = "AES/CBC/PKCS5Padding";
    private static final String secretKeyInstance = "PBKDF2WithHmacSHA1";
    private static final String plainText = "sampleText";
    private static final String AESSalt = "exampleSalt";
    private static final String initializationVector = "8119745113154120";

    private AppCompatImageView imageView1;
    private AppCompatImageView imageView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView1 = findViewById(R.id.imageView1);
        imageView2 = findViewById(R.id.imageView2);

        Benchmark benchmark = new Benchmark();



        benchmark.newRecord("getBitmapFromImageRes");
        Bitmap bitmap1 = getBitmapFromImageRes(R.drawable.img_plane);
        imageView1.setImageBitmap(bitmap1);

        benchmark.done();

        benchmark.newRecord("getBase64FromBitmap");
        String base64 = getBase64FromBitmap(bitmap1);
        benchmark.done();

        benchmark.newRecord("encryptString");
        String encrypted = encryptString(base64);
        benchmark.done();
        // upload

        // download
        benchmark.newRecord("decryptString");
        String decryptedBase64 = null;
        decryptedBase64 = decryptString(encrypted);
        benchmark.done();

        benchmark.newRecord("decodeBase64");
        Bitmap bitmap2 = decodeBase64(decryptedBase64);
        benchmark.done();

        imageView2.setImageBitmap(bitmap2);


        Log.d("pttt", benchmark.getLog());
    }

    public static Bitmap decodeBase64(String base64) {
        byte[] decodedBytes = Base64.decode(base64,0);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    private String decryptString(String textToDecrypt){
        String decryptedString="";
        try{
            byte[] encryted_bytes = Base64.decode(textToDecrypt, Base64.DEFAULT);
            SecretKeySpec skeySpec = new SecretKeySpec(getRaw(plainText, AESSalt), "AES");
            Cipher cipher = Cipher.getInstance(cypherInstance);
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, new IvParameterSpec(initializationVector.getBytes()));
            byte[] decrypted = cipher.doFinal(encryted_bytes);
            decryptedString = new String(decrypted, "UTF-8");
        }catch (Exception e) {
            e.printStackTrace();
        }
        return decryptedString;
    }

    private String encryptString(String textToEncrypt){
        String encryptedString="";
        try {
            SecretKeySpec skeySpec = new SecretKeySpec(getRaw(plainText, AESSalt), "AES");
            Cipher cipher = Cipher.getInstance(cypherInstance);
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, new IvParameterSpec(initializationVector.getBytes()));
            byte[] encrypted = cipher.doFinal(textToEncrypt.getBytes());
            encryptedString = Base64.encodeToString(encrypted, Base64.DEFAULT);
        }catch (Exception e) {
            e.printStackTrace();
        }
        return encryptedString;
    }

    private Bitmap getBitmapFromImageRes(int res) {
        return BitmapFactory.decodeResource(getResources(), res);
    }

    private String getBase64FromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteStream);
        byte[] byteArray = byteStream.toByteArray();
        String baseString = Base64.encodeToString(byteArray,Base64.DEFAULT);
        return baseString;
    }

    private static byte[] getRaw(String plainText, String salt) {
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance(secretKeyInstance);
            KeySpec spec = new PBEKeySpec(plainText.toCharArray(), salt.getBytes(), pswdIterations, keySize);
            return factory.generateSecret(spec).getEncoded();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return new byte[0];
    }

}









