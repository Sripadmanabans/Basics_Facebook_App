package com.example.sripadmanaban.basics;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.Request;
import com.facebook.RequestBatch;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphObject;
import com.facebook.widget.LoginButton;

/**
 * This is used for the Send BatchRequest Fragment
 * Created by Sripadmanaban on 2/17/2015.
 */
public class BatchRequestFragment extends Fragment {

    private static final String TAG = "BatchRequestFragment";

    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState sessionState, Exception e) {
            onSessionStateChange(session, sessionState, e);
        }
    };

    private UiLifecycleHelper uiHelper;

    private Button batchRequestButton;
    private TextView textViewResults;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uiHelper = new UiLifecycleHelper(getActivity(), callback);
        uiHelper.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login_batch_request, container, false);

        LoginButton loginButton = (LoginButton) view.findViewById(R.id.authButton);
        loginButton.setFragment(this);

        batchRequestButton = (Button) view.findViewById(R.id.batchRequestButton);
        batchRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doBatchRequest();
            }
        });

        return view;
    }

    private void onSessionStateChange(final Session session, SessionState sessionState, Exception exception) {
        if(sessionState.isOpened()) {
            Log.i(TAG, "Logged in...");
            batchRequestButton.setVisibility(View.VISIBLE);
        } else if(sessionState.isClosed()) {
            Log.i(TAG, "Logged out...");
            batchRequestButton.setVisibility(View.INVISIBLE);
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

    private void doBatchRequest() {
        textViewResults = (TextView) this.getView().findViewById(R.id.textViewResults);
        textViewResults.setText("");

        String[] requestIds = {"me", "100009161888838"};

        RequestBatch requestBatch = new RequestBatch();
        for(final String requestId : requestIds) {
            requestBatch.add(new Request(Session.getActiveSession(),
                    requestId, null, null, new Request.Callback() {
                @Override
                public void onCompleted(Response response) {
                    GraphObject graphObject = response.getGraphObject();
                    if(graphObject == null) {
                        Log.i(TAG, "fail");
                    }
                    String s = textViewResults.getText().toString();
                    if(graphObject != null) {
                        if(graphObject.getProperty("id") != null) {
                            s = s + String.format("%s: %s\n",
                                    graphObject.getProperty("id"),
                                    graphObject.getProperty("name"));

                        }
                    }

                    textViewResults.setText(s);
                }
            }));

        }
        requestBatch.executeAsync();
    }
}