import React from 'react';
import { Text, TouchableOpacity } from 'react-native';
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
            defaultValue: 'some',
            type: 'plain-text',
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
            defaultValue: 'some',
            type: 'secure-text',
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
    </>
  );
};
