import { StyleSheet, View } from 'react-native';
import { AlertsExamples } from './alerts';
import { SheetsExamples } from './sheets';

export default function App() {
  return (
    <View style={styles.container}>
      <AlertsExamples />
      <SheetsExamples />
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },
  box: {
    width: 60,
    height: 60,
    marginVertical: 20,
  },
});
