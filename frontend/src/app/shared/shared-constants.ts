import { environment } from '../../environments/environment';
export const PROFILE_COLORS = [
    '#0068ff',
    '#ffa500',
    '#349721',
    '#f81aa1',
    '#cbbeb5',
    '#654ef2',
    '#ff4040',
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

export const BASE_URL = "http://localhost:8080​​​​​​​​​​​​​​/v1/services";

export const API_END_POINT = {
    PLATO: {
        GET_SERVICES: {
            URL: BASE_URL,
            METHOD: HTTP_METHOD.GET
        },
        ADD_SERVICES: {
          URL: `${BASE_URL}${EndPoints.VERSION_SERVICES}`,
          METHOD: HTTP_METHOD.POST
      }
        
      }
}