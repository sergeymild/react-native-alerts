import { type KeyboardType, NativeModules, Platform } from 'react-native';
import type { AlertButton, AlertParams } from './base.types';

const LINKING_ERROR =
  `The package 'react-native-a' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo managed workflow\n';

const AlertManager = NativeModules.BaseAlert
  ? NativeModules.BaseAlert
  : new Proxy(
      {},
      {
        get() {
          throw new Error(LINKING_ERROR);
        },
      }
    );

export type AlertType = 'default' | 'plain-text' | 'secure-text';

type PromptParams = {
  title?: string;
  message?: string;
  buttons?: Array<AlertButton>;
  type: AlertType;
  theme?: 'dark' | 'light' | 'system';
  defaultValue?: string;
  keyboardType?: KeyboardType;
};

export const alert = {
  dismissTopPresented() {
    AlertManager.dismissTopPresented();
  },

  alert(params: AlertParams) {
    return new Promise((resolve) => {
      if (!params.buttons || params.buttons.length === 0) {
        params.buttons = [{ text: 'Ok', style: 'default', id: 'ok' }];
      }
      AlertManager.alertWithArgs(
        {
          title: params.title || '',
          message: params.message || undefined,
          type: 'default',
          theme: params.theme,
          buttons: params.buttons.map((b) => ({
            text: b.text,
            style: b.style,
            id: b.id,
          })),
        },
        (data: any) => {
          console.log('🍓[Alert.]', data);
          resolve(data);
        }
      );
    });
  },

  prompt(params: PromptParams) {
    return new Promise((resolve) => {
      if (!params.buttons || params.buttons.length === 0) {
        params.buttons = [{ text: 'Ok', style: 'default', id: 'ok' }];
      }
      AlertManager.alertWithArgs(
        {
          title: params.title || undefined,
          message: params.message || undefined,
          type: params.type || 'plain-text',
          defaultValue: params.defaultValue,
          keyboardType: params.keyboardType,
          theme: params.theme,
          buttons: params.buttons.map((b) => ({
            text: b.text,
            style: b.style,
            id: b.id,
          })),
        },
        (id: string, value: string | undefined) => {
          resolve({ id, value });
        }
      );
    });
  },
};
