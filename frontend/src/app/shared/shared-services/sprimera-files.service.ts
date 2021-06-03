import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ProfileSpecTO } from '../models/ProfileSpecTO';

@Injectable({
  providedIn: 'root'
})
export class SprimeraFilesService {

  private _base = "http://localhost:8080/v1/services/getFiles";
  

  constructor(private http:HttpClient) { }

  getFiles(service:string,branch:string,profile:string){
    //let modified_url = this._base_url_getFiles.concat(service.toString(),"/branches/",branch.toString(),"?profile=",profile.toString());
    return this.http.get<any[]>(`${this._base}/${service}/branches/${branch}`,{
      params:{
        profile
      }
    });
  }
  
}
