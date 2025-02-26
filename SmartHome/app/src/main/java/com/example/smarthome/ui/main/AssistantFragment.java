package com.example.smarthome.ui.main;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smarthome.R;
import com.example.smarthome.adapter.MessageAdapter;
import com.example.smarthome.data.api.ApiChat;
import com.example.smarthome.data.model.Message;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AssistantFragment extends Fragment {

    private RecyclerView recyclerView;
    private EditText message_text_text;
    private ImageView send_btn;
    private List<Message> messageList = new ArrayList<>();
    private MessageAdapter messageAdapter;

    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private final OkHttpClient client = new OkHttpClient();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_assistant, container, false);

        message_text_text = view.findViewById(R.id.message_text_text);
        send_btn = view.findViewById(R.id.send_btn);
        recyclerView = view.findViewById(R.id.recyclerView);

        // Cấu hình RecyclerView
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        messageAdapter = new MessageAdapter(messageList);
        recyclerView.setAdapter(messageAdapter);

        send_btn.setOnClickListener(v -> {
            String question = message_text_text.getText().toString().trim();
            if (!question.isEmpty()) {
                send_btn.setEnabled(false); // Vô hiệu hóa nút gửi khi đang gửi request
                addToChat(question, Message.SEND_BY_ME);
                message_text_text.setText("");
                callAPI(question);
            } else {
                Toast.makeText(requireContext(), "Vui lòng nhập câu hỏi!", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    @SuppressLint("NotifyDataSetChanged")
    private void addToChat(String message, String sendBy) {
        if (!isAdded()) return; // Kiểm tra Fragment đã được gắn vào Activity chưa
        requireActivity().runOnUiThread(() -> {
            messageList.add(new Message(message, sendBy));
            messageAdapter.notifyDataSetChanged();
            if (recyclerView != null) {
                recyclerView.smoothScrollToPosition(messageAdapter.getItemCount());
            }
        });
    }

    private void addResponse(String response) {
        if (!messageList.isEmpty()) {
            messageList.remove(messageList.size() - 1);
        }
        addToChat(response, Message.SEND_BY_BOT);
        requireActivity().runOnUiThread(() -> send_btn.setEnabled(true)); // Bật lại nút gửi sau khi nhận phản hồi
    }

    private void callAPI(String question) {
        if (ApiChat.API_KEY == null || ApiChat.API_KEY.isEmpty()) {
            addResponse("🚨 API key is missing!");
            return;
        }

        messageList.add(new Message("✍️ AI đang soạn tin...", Message.SEND_BY_BOT));

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("model", "gpt-4o"); // ✅ Sử dụng model phù hợp
            JSONArray messagesArray = new JSONArray();
            messagesArray.put(new JSONObject().put("role", "system").put("content", "Bạn là một trợ lý thông minh."));
            messagesArray.put(new JSONObject().put("role", "user").put("content", question));

            jsonBody.put("messages", messagesArray); // ✅ Dùng đúng định dạng
            jsonBody.put("max_tokens", 200);
            jsonBody.put("temperature", 0.7);
        } catch (JSONException e) {
            addResponse("❌ Lỗi tạo request JSON!");
            return;
        }

        RequestBody requestBody = RequestBody.create(jsonBody.toString(), JSON);
        Request request = new Request.Builder()
                .url(ApiChat.API_URL) // ✅ Đảm bảo API URL đúng
                .header("Authorization", "Bearer " + ApiChat.API_KEY)
                .header("Content-Type", "application/json")
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                addResponse("❌ Lỗi kết nối: " + e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                requireActivity().runOnUiThread(() -> send_btn.setEnabled(true)); // Bật lại nút gửi

                if (!response.isSuccessful() || response.body() == null) {
                    addResponse("⚠️ Lỗi server: " + response.code());
                    return;
                }

                try {
                    String responseBody = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseBody);

                    if (jsonObject.has("error")) {
                        JSONObject error = jsonObject.getJSONObject("error");
                        String errorMessage = error.getString("message");
                        addResponse("⚠️ Lỗi API: " + errorMessage);
                        return;
                    }

                    JSONArray choices = jsonObject.getJSONArray("choices");
                    String result = choices.getJSONObject(0).getJSONObject("message").getString("content").trim();
                    addResponse(result);
                } catch (JSONException e) {
                    addResponse("❌ Lỗi xử lý phản hồi API!");
                }
            }
        });
    }
}
