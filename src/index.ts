

import {NativeModules} from "react-native";
import { DEFAULT_OPTIONS, MODULE_NAME, type LOCATION_RESPONSE, type location_options } from "./utils";

const {[MODULE_NAME]: modules} = NativeModules;

class RNLocation{
    static getCurrentPostion = async(opts: location_options = DEFAULT_OPTIONS):Promise<LOCATION_RESPONSE> => {
   
        try {
            let res: LOCATION_RESPONSE = await modules.getCurrentPosition(opts);
            return res;
          } catch (error) {
            throw error;
          }
    }
}

export default RNLocation;