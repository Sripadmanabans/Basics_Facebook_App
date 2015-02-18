package com.example.sripadmanaban.basics;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.LoginButton;
import com.facebook.widget.WebDialog;

/**
 * This fragment is used to send request
 * Created by Sripadmanaban on 2/18/2015.
 */
public class SendRequestFragment extends Fragment {
    private static final String TAG = "SendRequestFragment";
    private String requestId;

    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState sessionState, Exception e) {
            onSessionStateChange(session, sessionState, e);
        }
    };

    private UiLifecycleHelper uiHelper;

    private Button sendRequestButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uiHelper = new UiLifecycleHelper(getActivity(), callback);
        uiHelper.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_send_request, container, false);

        LoginButton loginButton = (LoginButton) view.findViewById(R.id.authButton);
        loginButton.setFragment(this);

        sendRequestButton = (Button) view.findViewById(R.id.sendRequestButton);
        sendRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRequestDialog();
            }
        });

        return view;
    }

    private void onSessionStateChange(final Session session, SessionState sessionState, Exception exception) {
        if(sessionState.isOpened() && requestId != null) {
            Toast.makeText(getActivity().getApplicationContext(), "Incoming Request",
                    Toast.LENGTH_SHORT).show();
            requestId = null;
        }
        if(sessionState.isOpened()) {
            Log.i(TAG, "Logged in...");
            sendRequestButton.setVisibility(View.VISIBLE);
        } else if(sessionState.isClosed()) {
            Log.i(TAG, "Logged out...");
            sendRequestButton.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //Check for an incoming notification. Save the info
        Uri intentUri = getActivity().getIntent().getData();
        if(intentUri != null) {
            String requestIdParam = intentUri.getQueryParameter("request_ids");
            if(requestIdParam != null) {
                String array[] = requestIdParam.split(",");
                requestId = array[0];
                Log.i(TAG, "Request Id: " + requestId);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        Session session = Session.getActiveSession();
        if(session != null &&
                (session.isOpened() || session.isClosed())) {
            onSessionStateChange(session, session.getState(), null);
        }

        uiHelper.onResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
    }

    private void sendRequestDialog() {
        Bundle params = new Bundle();
        params.putString("message", "Learn how to make your Android Apps social");
        params.putString("data",
                "{\"badge_of_awesomeness\":\"1\","+
                "\"social_karma\":\"5\"}");

        WebDialog requestDialog = (new WebDialog.RequestsDialogBuilder(getActivity(),
                Session.getActiveSession(),
                params))
                .setOnCompleteListener(new WebDialog.OnCompleteListener() {
                    @Override
                    public void onComplete(Bundle values, FacebookException error) {
                        if(error != null) {
                            if(error instanceof FacebookOperationCanceledException) {
                                Toast.makeText(getActivity().getApplicationContext(),
                                        "Request Cancelled",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getActivity().getApplicationContext(),
                                        "Network Error",
                                        Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            final String requestId = values.getString("request");
                            if(requestId != null) {
                                Toast.makeText(getActivity().getApplicationContext(),
                                        "Request Sent",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getActivity().getApplicationContext(),
                                        "Request Cancelled",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                })
                .build();
        requestDialog.show();
    }

}
