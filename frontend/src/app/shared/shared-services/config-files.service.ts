import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import * as yaml from 'yaml';

@Injectable({
  providedIn: 'root'
})
export class ConfigFilesService {

  private _base_url = "http://localhost:8080/v1/services";

  constructor(private http: HttpClient) { }
  getFile(service:any, type:any, branch:any, profile:any){
    return this.http.get(`${this._base_url}/${service}/branches/${branch}`,{
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
