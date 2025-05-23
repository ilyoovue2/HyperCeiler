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
package com.sevtinge.hyperceiler.hook.module.hook.systemsettings;

import android.content.Context;

import com.sevtinge.hyperceiler.hook.module.base.BaseHook;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;

import de.robv.android.xposed.XposedHelpers;

public class LanguageMenuShowAllApps extends BaseHook {
    @Override
    public void init() throws NoSuchMethodException {
        findAndHookMethod("android.util.FeatureFlagUtils", "isEnabled", Context.class, String.class, new MethodHook() {
            @Override
            protected void before(MethodHookParam param) {
                String param1 = (String) param.args[1];
                if (Objects.equals(param1, "settings_app_locale_opt_in_enabled")) param.setResult(false);
            }
        });

        findAndHookMethod("com.android.settings.applications.AppLocaleUtil", "isAppLocaleSupported", Context.class, String.class, new MethodHook() {
            @Override
            protected void before(MethodHookParam param) {
                param.setResult(true);
            }
        });

        /*允许对小米定义的列表中的应用进行语言调整*/
        findAndHookMethod("com.android.settings.localepicker.LocalePickerWithRegion", "filterTheLanguagesNotSupportedInApp", boolean.class, HashSet.class, new MethodHook() {
            @Override
            protected void before(MethodHookParam param) {
                param.setResult(XposedHelpers.getObjectField(param.thisObject, "mLocaleList"));
            }
        });

        /*允许对 系统应用、平台签名的应用（如GMS等） 进行语言调整*/
        findAndHookMethod("com.android.settings.applications.AppLocaleUtil", "canDisplayLocaleUi", Context.class, String.class, List.class, new MethodHook() {
            @Override
            protected void before(MethodHookParam param) {
                param.setResult(true);
            }
        });
    }
}
