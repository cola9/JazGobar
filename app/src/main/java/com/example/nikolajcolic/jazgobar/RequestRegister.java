package com.example.nikolajcolic.jazgobar;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by nikolajcolic on 11/09/2017.
 */

public class RequestRegister extends StringRequest {

    private static final String REGISTER_REQUEST_URL = "https://jazgobar.000webhostapp.com/Register.php";
    private Map<String,String> params;

    public RequestRegister(String username, String email, String password, Response.Listener<String> listener){
        super(Method.POST, REGISTER_REQUEST_URL, listener, null);
        params=new HashMap<>();
        params.put("email", email);
        params.put("username", username);
        params.put("password", password);
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
