package com.example.sripadmanaban.basics;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.LoggingBehavior;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.Settings;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphObject;
import com.facebook.model.GraphObjectList;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Sripadmanaban on 2/17/2015.
 */
public class LoginFragment extends Fragment {

    private static final String TAG = "LoginFragment";

    private TextView userInfoTextView;

    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState sessionState, Exception e) {
            onSessionStateChange(session, sessionState, e);
        }
    };

    private UiLifecycleHelper uiHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uiHelper = new UiLifecycleHelper(getActivity(), callback);
        uiHelper.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        LoginButton loginButton = (LoginButton) view.findViewById(R.id.authButton);
        loginButton.setFragment(this);
        loginButton.setReadPermissions(Arrays.asList("user_location", "user_birthday", "user_likes"));

        userInfoTextView = (TextView) view.findViewById(R.id.userInfoTextView);

        return view;
    }

    private void onSessionStateChange(final Session session, SessionState sessionState, Exception exception) {
        if(sessionState.isOpened()) {
            Log.i(TAG, "Logged in...");
            userInfoTextView.setVisibility(View.VISIBLE);

            // Request user data and show the results
            Request meRequest = Request.newMeRequest(session, new Request.GraphUserCallback() {
                @Override
                public void onCompleted(GraphUser user, Response response) {
                    if(user != null) {
                        // Display the parsed user info
                        userInfoTextView.setText(buildUserInfoDisplay(user));
                    }
                }
            });

            Settings.addLoggingBehavior(LoggingBehavior.REQUESTS);

            Request.executeBatchAsync(meRequest);

        } else if(sessionState.isClosed()) {
            Log.i(TAG, "Logged out...");
            userInfoTextView.setVisibility(View.INVISIBLE);
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

    private String buildUserInfoDisplay(GraphUser user) {
        StringBuilder userInfo = new StringBuilder("");

        // Example: typed access (name)
        // - no special permissions required
        userInfo.append(String.format("Name: %s\n\n",
                user.getName()));

        // Example: typed access (birthday)
        // - requires birthday permission
        userInfo.append(String.format("Birthday: %s\n\n",
                user.getBirthday()));

        // Example: partially types access, to location field
        // name key (location)
        // - requires user_location permission
        userInfo.append(String.format("Location: %s\n\n",
                user.getLocation().getProperty("name")));

        // Example: access via property name (locale)
        // - no special permission required
        userInfo.append(String.format("Locale: %s\n\n",
                user.getProperty("locale")));

        // Doesn't work maybe needs that review
        // Example: access via key for array (languages)
        // - requires user_likes permission
        /*JSONArray languages = (JSONArray) user.getProperty("languages");
        if(languages.length() > 0) {
            ArrayList<String> languageNames = new ArrayList<>();
            for(int i = 0; i < languages.length(); i++) {
                JSONObject language = languages.optJSONObject(i);
                languageNames.add(language.optString("name"));
            }
            userInfo.append(String.format("Languages: %s\n\n",
                    languageNames.toString()));
        }*/

        // Using interface stuff to get the languages
        /*JSONArray languages = (JSONArray) user.getProperty("languages");
        if(languages.length() > 0) {
            ArrayList<String> languageNames = new ArrayList<>();

            // Get the data from creating a typed interface
            // for the language data.
            GraphObjectList<MyGraphLanguage> graphObjectLanguages =
                    GraphObject.Factory.createList(languages, MyGraphLanguage.class);

            // Iterate through the list of languages
            for(MyGraphLanguage language : graphObjectLanguages) {
                // Add the language name to a list. Use the name
                // getter method to get access to the name field.
                languageNames.add(language.getName());
            }

            userInfo.append(String.format("Languages : %s\n\n",
                    languageNames.toString()));
        }*/

        // Get a list of languages from an interface that
        // extends the GraphUser interface and that returns
        // a GraphObject list of MyGraphLanguage objects
        GraphObjectList<MyGraphLanguage> languages =
                (user.cast(MyGraphUser.class)).getLanguages();
        if(languages.size() > 0) {
            ArrayList<String> languageNames = new ArrayList<>();
            // Iterate through the list of languages
            for(MyGraphLanguage language : languages) {
                languageNames.add(language.getName());
            }

            userInfo.append(String.format("Languages: %s\n\n",
                    languageNames.toString()));
        }

        return userInfo.toString();
    }

    private interface MyGraphLanguage extends GraphObject {
        // Getter for the ID field
        String getId();

        // Getter for the Name field
        String getName();
    }

    private interface MyGraphUser extends GraphUser {
        // Create a setter to enable easy extraction of the languages field
        GraphObjectList<MyGraphLanguage> getLanguages();
    }

}
