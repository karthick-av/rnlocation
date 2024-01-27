

import { NativeModules } from 'react-native';
import { DEFAULT_OPTIONS, MODULE_NAME, type LOCATION_RESPONSE, type location_options } from './utils';

const { [MODULE_NAME]: modules } = NativeModules;

class RNLocation {
  static getCurrentPostion = async (opts: location_options = DEFAULT_OPTIONS): Promise<LOCATION_RESPONSE> => {

    try {
      let res: LOCATION_RESPONSE = await modules.getCurrentPosition(opts);
      return res;
    } catch (error) {
      throw error;
    }
  }

  static isGPSenabled = async (): Promise<boolean> => {
    return await modules.isGPSenabled();
  }


  static requestPermissionIfNeeded = async (): Promise<boolean> => {
    return await modules.requestPermissionIfNeeded();
  }
  static promptGPSIfNeeded = (): void => {
    modules.promptGPSIfNeeded();
  }

  static isServiceRunning = async (): Promise<boolean> => {
    return await modules.isServiceRunning();
  }

  static startBackgroundService = (): void => {
    modules.startBackgroundService();
  }
  static StopBackgroundService = (): void => {
    modules.StopBackgroundService();
  }

  static WifiSettings = (): void => {
    modules.WifiSettings();
  }

  static getUniqueId = async():Promise<String> => {
    try{
      return await modules.getUniqueId();
    }catch(e){
      throw e;
    }
  }
}

export default RNLocation;