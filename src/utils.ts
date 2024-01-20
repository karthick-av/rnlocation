
export type location_options ={
    timeout: number
} 

export const DEFAULT_OPTIONS: location_options = {
    timeout: 60000
  };


  export type LOCATION_RESPONSE = {
    accuracy: number
    altitude: number
    bearing: number
    latitude: number
    longitude: number
    provider: string
    speed: number
    time: number
  }


  export const MODULE_NAME = "RNlocationNativeModule";