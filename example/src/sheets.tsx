import React from 'react';
import { Text, TouchableOpacity, View } from 'react-native';
import { sheetAlert } from 'react-native-alerts';

export const SheetsExamples: React.FC = () => {
  return (
    <View style={{ marginTop: 32 }}>
      <TouchableOpacity
        onPress={async () => {
          const id = await sheetAlert.show({
            theme: 'light',
            message: {
              text: 'Some awesome message will be here',
              appearance: { fontSize: 14 },
            },
            title: {
              text: 'Some awesome title',
              appearance: {
                fontSize: 20,
              },
            },
            buttons: [
              {
                text: 'Edit',
                id: 'someId',
                style: 'default',
              },
              {
                text: 'Delete',
                id: 'someId',
                style: 'destructive',
              },
              {
                text: 'Cancel',
                id: 'someId',
                style: 'cancel',
              },
            ],
          });
          console.log('🍓[App.]', id);
        }}
      >
        <Text children={'Base Light Alert'} />
      </TouchableOpacity>

      <TouchableOpacity
        onPress={async () => {
          const id = await sheetAlert.show({
            theme: 'light',
            message: {
              text: 'Some awesome message will be here',
              appearance: { fontSize: 14 },
            },
            title: {
              text: 'Some awesome title',
              appearance: {
                fontSize: 20,
              },
            },
            buttons: [
              {
                text: 'Edit',
                id: 'someId',
                style: 'default',
              },
              {
                text: 'Delete',
                id: 'someId',
                style: 'destructive',
              },
              {
                text: 'Cancel',
                id: 'someId',
                style: 'cancel',
              },
            ],
          });
          console.log('🍓[App.]', id);
        }}
      >
        <Text children={'Base Dark Alert'} />
      </TouchableOpacity>
    </View>
  );
};
