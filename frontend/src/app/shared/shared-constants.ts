import { environment } from 'src/environments/environment';
export const APPLICATION = {
  APP_NAME: 'PLATO'
}
export const WARNING_DIALOG_PARAM = {
  data: "",
  height: '250px',
  width: '400px',
  disableClose:true
}
export const PROFILE_COLORS = [
    '#E99497',
    '#F3C583',
    '#B3E283',
    '#B6C9F0',
    '#A58FAA',
    '#CAF7E3',
    '#FDBAF8',
  ];
  
class EndPoints {
  static VERSION = '/v1';
  static SERVICES = '/services';
  static VERSION_SERVICES = EndPoints.VERSION+EndPoints.SERVICES;
}

export const HTTP_METHOD = {
    DELETE: 'DELETE',
    GET: 'GET',
    POST: 'POST',
    PUT: 'PUT',
    UPDATE: 'UPDATE',
    UPLOAD: 'UPLOAD',
    UPLOAD_PUT: 'UPLOAD_PUT'
};

export const ROUTER_DIRECT = {
    PLATO: ''
};

export const SUPPRESSED_EXCEPTIONS = [
];

export const BASE_URL = environment.PLATO_API;

export const API_END_POINT = {
    PLATO: {
        GET_SERVICES: {
            URL: `${BASE_URL}${EndPoints.VERSION_SERVICES}`,
            METHOD: HTTP_METHOD.GET
        },
        ADD_SERVICES: {
          URL: `${BASE_URL}${EndPoints.VERSION_SERVICES}`,
          METHOD: HTTP_METHOD.POST
        },
        
      }
}