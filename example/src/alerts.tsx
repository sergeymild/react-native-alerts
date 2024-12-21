import React from 'react';
import { Alert, Text, TouchableOpacity } from 'react-native';
import { alert } from 'react-native-alerts';

export const AlertsExamples: React.FC = () => {
  return (
    <>
      <TouchableOpacity
        onPress={async () => {
          const id = await alert.alert({
            theme: 'light',
            title: 'Title',
            message: 'Message',
            buttons: [
              { text: 'Default', style: 'default', id: 'base' },
              { text: 'Remove', style: 'destructive', id: 'remove' },
              { text: 'Cancel', style: 'cancel', id: 'cancel' },
            ],
          });
          console.log('🍓[App.]', id);
        }}
      >
        <Text children={'Base Light Alert'} />
      </TouchableOpacity>
      <TouchableOpacity
        onPress={() => {
          alert.alert({
            theme: 'dark',
            title: 'Title',
            message: 'Message',
            buttons: [
              { text: 'Default', style: 'default', id: 'base' },
              { text: 'Remove', style: 'destructive', id: 'remove' },
              { text: 'Cancel', style: 'cancel', id: 'cancel' },
            ],
          });
        }}
      >
        <Text children={'Base Dark Alert'} />
      </TouchableOpacity>

      <TouchableOpacity
        onPress={async () => {
          const data = await alert.prompt({
            theme: 'light',
            title: 'Title',
            message: 'Message',
            fields: [
              {
                placeholder: 'some placeholder',
                id: 'field-id',
                keyboardType: 'decimal-pad',
              },
              {
                placeholder: 'some placeholder',
                id: 'field-id2',
                defaultValue: 'Value',
              },
              {
                id: 'field-id3',
                security: true,
              },
            ],
            buttons: [
              { text: 'Default', style: 'default', id: 'base' },
              { text: 'Remove', style: 'destructive', id: 'remove' },
              { text: 'Cancel', style: 'cancel', id: 'cancel' },
            ],
          });
          console.log('🍓[App.]', data);
        }}
      >
        <Text children={'Base Light plain Input Alert'} />
      </TouchableOpacity>

      <TouchableOpacity
        onPress={async () => {
          const data = await alert.prompt({
            theme: 'dark',
            title: 'Title',
            message: 'Message',
            fields: [
              {
                placeholder: 'some placeholder',
                id: 'field-id',
                keyboardType: 'decimal-pad',
                defaultValue: '',
                security: false,
              },
            ],
            buttons: [
              { text: 'Default', style: 'default', id: 'base' },
              { text: 'Remove', style: 'destructive', id: 'remove' },
              { text: 'Cancel', style: 'cancel', id: 'cancel' },
            ],
          });
          console.log('🍓[App.]', data);
        }}
      >
        <Text children={'Base dark secure Input Alert'} />
      </TouchableOpacity>

      <TouchableOpacity
        onPress={async () => {
          Alert.alert('title', 'message', [{ text: 'ok' }]);
        }}
      >
        <Text children={'Rn Alert'} />
      </TouchableOpacity>
    </>
  );
};
