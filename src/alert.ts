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
    AlertManager.dismissTopPresented();
  },

  alert(params: AlertParams) {
    return new Promise<string | undefined>((resolve) => {
      if (!params.buttons || params.buttons.length === 0) {
        params.buttons = [{ text: 'Ok', style: 'default', id: 'ok' }];
      }
      AlertManager.alertWithArgs(
        {
          type: 'default',
          title: params.title || '',
          message: params.message || undefined,
          theme: params.theme,
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
      AlertManager.alertWithArgs(
        {
          type: 'prompt',
          title: params.title || undefined,
          message: params.message || undefined,
          theme: params.theme,
          fields: params.fields,
          buttons: params.buttons.map((b) => ({
            text: b.text,
            style: b.style,
            id: b.id,
          })),
        },
        (id: string, values: Record<string, string> | undefined) => {
          resolve({ id, values });
        }
      );
    });
  },
};
