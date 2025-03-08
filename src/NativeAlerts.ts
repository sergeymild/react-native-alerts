import { type TurboModule, TurboModuleRegistry } from 'react-native';
import type {
  Int32,
  UnsafeObject,
} from 'react-native/Libraries/Types/CodegenTypes';

type Color = UnsafeObject;

export interface Spec extends TurboModule {
  dismissTopPresented(): void;
  alertWithArgs(
    params: {
      type: string;
      title?: string;
      message?: string;
      theme?: string;
      buttons: { text: string; style: string; id: string }[];
      fields: {
        placeholder: string;
        id: string;
        keyboardType?: string;
        defaultValue?: string;
        security?: boolean;
      }[];
    },
    callback: (id: string, values: UnsafeObject | undefined) => void
  ): void;

  bottomSheetAlertWithArgs(
    params: {
      iosTintColor?: Color;
      theme?: string;
      buttonsBorderRadius?: Int32;
      cancelButtonBorderRadius?: Int32;
      title?: {
        text: string;
        appearance?: {
          textAlign?: string;
          fontSize?: number;
          color?: Color;
          fontFamily?: string;
        };
      };

      message?: {
        text: string;
        appearance?: {
          textAlign?: string;
          fontSize?: number;
          color?: Color;
          fontFamily?: string;
        };
      };

      buttons: {
        text: string;
        style?: string;
        id?: string;
        icon?: { type: string; icon: UnsafeObject };
        appearance?: {
          textAlign?: string;
          fontSize?: number;
          color?: Color;
          fontFamily?: string;
        };
      }[];
    },
    callback: (index: number) => void
  ): void;
}

export default TurboModuleRegistry.getEnforcing<Spec>('Alerts');
