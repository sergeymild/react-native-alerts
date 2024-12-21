import { type KeyboardType } from 'react-native';
import type { AlertButton, AlertParams } from './base.types';

import RNAlerts from './spec/NativeAlerts';

type PromptParams = {
  title?: string;
  message?: string;
  buttons: Array<AlertButton>;
  fields: Array<{
    placeholder: string;
    id: string;
    keyboardType?: KeyboardType;
    defaultValue?: string;
    security?: boolean;
  }>;
  theme?: 'dark' | 'light' | 'system';
};

export const alert = {
  dismissTopPresented() {
    RNAlerts.dismissTopPresented();
  },

  alert(params: AlertParams) {
    return new Promise<string | undefined>((resolve) => {
      if (!params.buttons || params.buttons.length === 0) {
        params.buttons = [{ text: 'Ok', style: 'default', id: 'ok' }];
      }
      RNAlerts.alertWithArgs(
        {
          type: 'default',
          title: params.title || undefined,
          message: params.message || undefined,
          theme: params.theme,
          fields: [],
          buttons: params.buttons.map((b) => ({
            text: b.text,
            style: b.style,
            id: b.id,
          })),
        },
        (data: string | undefined) => {
          resolve(data);
        }
      );
    });
  },

  prompt(params: PromptParams) {
    return new Promise<{
      id: string;
      values: Record<string, string> | undefined;
    }>((resolve) => {
      if (!params.buttons || params.buttons.length === 0) {
        params.buttons = [{ text: 'Ok', style: 'default', id: 'ok' }];
      }
      RNAlerts.alertWithArgs(
        {
          type: 'prompt',
          title: params.title || undefined,
          message: params.message || undefined,
          theme: params.theme,
          fields: params.fields ?? [],
          buttons: params.buttons.map((b) => ({
            text: b.text,
            style: b.style,
            id: b.id,
          })),
        },
        (id: string, values: object | undefined) => {
          resolve({ id, values: values as Record<string, string> });
        }
      );
    });
  },
};
