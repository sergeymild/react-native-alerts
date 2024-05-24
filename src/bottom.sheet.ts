import {
  Image,
  type ImageRequireSource,
  NativeModules,
  processColor,
} from 'react-native';

export type BottomSheetAlertButtonStyle = 'default' | 'destructive' | 'cancel';
type Appearance = {
  textAlign?: 'center';
  fontSize?: number;
  color?: string;
  fontFamily?: string;
};
export interface BottomSheetAlertButton {
  readonly text: string;
  readonly style?: BottomSheetAlertButtonStyle;
  readonly data?: any;
  readonly icon?: ImageRequireSource;
  readonly appearance?: Appearance;
}

interface BottomSheetAlertProperties {
  iosTintColor?: string;
  readonly title?: {
    text: string;
    readonly appearance?: Appearance;
  };
  readonly message?: {
    text: string;
    readonly appearance?: Appearance;
  };
  readonly buttons: BottomSheetAlertButton[];
  readonly theme?: 'light' | 'dark';
  readonly buttonsBorderRadius?: number;
  readonly cancelButtonBorderRadius?: number;
}

export class BottomSheetAlert {
  static show(
    properties: BottomSheetAlertProperties
  ): Promise<BottomSheetAlertButton | undefined> {
    return new Promise((resolve) => {
      NativeModules.BottomSheetAlert.show(
        {
          ...properties,
          iosTintColor: processColor(properties.iosTintColor),
          title: !properties.title
            ? undefined
            : {
                text: properties.title.text,
                appearance: !properties.title.appearance
                  ? undefined
                  : {
                      ...properties.title.appearance,
                      color: processColor(properties.title.appearance.color),
                    },
              },
          message: !properties.message
            ? undefined
            : {
                text: properties.message.text,
                appearance: !properties.message.appearance
                  ? undefined
                  : {
                      ...properties.message.appearance,
                      color: processColor(properties.message.appearance.color),
                    },
              },
          buttons: properties.buttons.map((b) => ({
            ...b,
            icon: b.icon ? Image.resolveAssetSource(b.icon).uri : undefined,
            appearance: !b.appearance
              ? undefined
              : {
                  ...b.appearance,
                  color: processColor(b.appearance.color),
                },
          })),
        },
        (index: number) => {
          if (index === -1) return resolve(undefined);
          const selected = properties.buttons[index];
          resolve(selected);
        }
      );
    });
  }
}
