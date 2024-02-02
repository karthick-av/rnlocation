

import { NativeModules, Platform } from 'react-native';
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
   if(Platform.OS == "ios") return false;
    return await modules.isGPSenabled();
  }


  static requestPermissionIfNeeded = async (): Promise<boolean> => {
    return await modules.requestPermissionIfNeeded();
  }
  static promptGPSIfNeeded = (): void => {
    if(Platform.OS == "ios")return;
    modules.promptGPSIfNeeded();
  }

  static isServiceRunning = async (): Promise<boolean> => {
    if(Platform.OS == "ios") return false;
    return await modules.isServiceRunning();
  }

  static startBackgroundService = (): void => {
    if(Platform.OS == "ios")return;
   modules.startBackgroundService();
  }
  static StopBackgroundService = (): void => {
    if(Platform.OS == "ios") return;
    modules.StopBackgroundService();
  }

  static WifiSettings = (): void => {
    if(Platform.OS == "ios") return;
    modules.WifiSettings();
  }

  static getUniqueId = async():Promise<String> => {
    if(Platform.OS == "ios") return "";
   try{
      return await modules.getUniqueId();
    }catch(e){
      throw e;
    }
  }

  static clear = (): void => {
    if(Platform.OS == "ios") return;
   modules.clear();
  }
}

export default RNLocation;