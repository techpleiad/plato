import { HttpClient } from '@angular/common/http';
import { Inject, Injectable } from '@angular/core';
import * as yaml from 'yaml';

@Injectable({
  providedIn: 'root'
})
export class ConfigFilesService {

  private BASE: any;
  constructor(private http: HttpClient, @Inject('API_END_POINT') private API_END_POINT: any) {
    this.BASE = API_END_POINT.PLATO;
   }
  getFile(service:any, type:any, branch:any, profile:any){
    return this.http.get(`${this.BASE.GET_SERVICES.URL}/${service}/branches/${branch}`,{
      params:{
        format:"yaml",
        type,
        profile
      },
      responseType: 'text'
    });
  }

  

  yamlToJson(temp: string){
    return yaml.parse(temp);
  }
}
