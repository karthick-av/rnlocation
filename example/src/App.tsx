import * as React from 'react';

import { StyleSheet, View, Text, Button } from 'react-native';
import RNLocation from 'rnlocation';
import type { LOCATION_RESPONSE } from '../../src/utils';
export default function App() {
 
  return (
    <View style={styles.container}>
     <Button 
     title='Location'
     onPress={() => {
      RNLocation.getCurrentPostion().then((res: LOCATION_RESPONSE) => {
        console.log(res)
      })
     }}
     />
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
