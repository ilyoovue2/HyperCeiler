/*
 * This file is part of HyperCeiler.

 * HyperCeiler is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.

 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.

 * Copyright (C) 2023-2025 HyperCeiler Contributions
 */
package com.sevtinge.hyperceiler.safemode;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.sevtinge.hyperceiler.ui.R;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;

import fan.appcompat.app.AppCompatActivity;

public class ExceptionCrashActivity extends AppCompatActivity implements View.OnLongClickListener {
    private String fullMsg;
    private String stackMsg;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crash);
        getAppCompatActionBar().setTitle("Ah？出错了");

        Intent intent = getIntent();
        Throwable throwable = intent.getSerializableExtra("crashInfo", Throwable.class);
        if (throwable == null) return;

        String message = throwable.getMessage();
        String exceptionType = throwable.getClass().getName();
        StackTraceElement element = throwable.getStackTrace()[0];
        String fileName = element.getFileName();
        String className = element.getClassName();
        String methodName = element.getMethodName();
        int lineNumber = element.getLineNumber();
        Date timestamp = new Date();

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        throwable.printStackTrace(pw);
        String fullDetail = sw.toString();

        TextView msgView = findViewById(R.id.message);
        fullMsg = "异常信息：" + message +
            "\n异常类型：" + exceptionType +
            "\n文件名：" + fileName +
            "\n抛出类：" + className +
            "\n抛出方法：" + methodName +
            "\n行号：" + lineNumber +
            "\n记录时间：" + timestamp;
        msgView.setText(fullMsg);

        TextView stackView = findViewById(R.id.stack);
        stackMsg = "详细信息：\n" + fullDetail;
        stackView.setText(stackMsg);

        msgView.setOnLongClickListener(this);
        stackView.setOnLongClickListener(this);
    }

    @Override
    public boolean onLongClick(View v) {
        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("text", fullMsg + "\n" + stackMsg);
        cm.setPrimaryClip(clip);
        Toast.makeText(v.getContext(), "已复制到剪贴板", Toast.LENGTH_SHORT).show();

        return true;
    }
}
