import { HttpClient } from '@angular/common/http';
import { Inject, Injectable } from '@angular/core';
import { ProfileSpecTO } from '../models/ProfileSpecTO';

@Injectable({
  providedIn: 'root'
})
export class SprimeraFilesService {
  private BASE: any;

  constructor(private http: HttpClient, @Inject('API_END_POINT') private API_END_POINT: any) {
    this.BASE = API_END_POINT.PLATO; 
   }

  getFiles(service:string,branch:string,profile:string){
    return this.http.get<any[]>(`${this.BASE.GET_SERVICES.URL}/getFiles/${service}/branches/${branch}`,{
      params:{
        profile
      }
    });
  }
  
}
