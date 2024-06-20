import {
  Image,
  type ImageRequireSource,
  NativeModules,
  processColor,
} from 'react-native';

export type BottomSheetAlertButtonStyle = 'default' | 'destructive' | 'cancel';
type Appearance = {
  textAlign?: 'center' | 'left';
  fontSize?: number;
  color?: string;
  fontFamily?: string;
};

type AssetButtonIcon = {type: 'asset', icon: string}
type DrawableButtonIcon = {type: 'drawable', icon: string}
type ResourceButtonIcon = {type: 'require', icon: ImageRequireSource}
type ButtonIcon = AssetButtonIcon | DrawableButtonIcon | ResourceButtonIcon

export interface BottomSheetAlertButton {
  readonly text: string;
  readonly style?: BottomSheetAlertButtonStyle;
  readonly id?: any;
  readonly icon?: ButtonIcon;
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

function processIcon(icon: ButtonIcon | undefined) {
  try {
    if (!icon) return undefined
    if (icon.type === 'require') {
      return {type: 'drawable', icon: Image.resolveAssetSource(icon.icon as number).uri}
    }
    return icon
  } catch (e) {
    console.warn('[BottomSheet.processIcon.errro]', e)
  }
  return undefined
}

export class sheetAlert {
  static show(
    properties: BottomSheetAlertProperties
  ): Promise<string | undefined> {
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
            icon: processIcon(b.icon),
            appearance: !b.appearance
              ? undefined
              : {
                ...b.appearance,
                color: processColor(b.appearance.color),
                textAlign: b.appearance.textAlign ?? 'center',
              },
          })),
        },
        (index: number) => {
          if (index === -1) return resolve(undefined);
          const selected = properties.buttons[index];
          if (!selected) return resolve(undefined);
          resolve(selected.id);
        }
      );
    });
  }
}
