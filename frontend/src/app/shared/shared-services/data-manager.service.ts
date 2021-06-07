import { HttpClient } from '@angular/common/http';
import { Inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { microService } from 'src/app/microService';
import * as yaml from 'yaml';


@Injectable({
  providedIn: 'root'
})
export class DataManagerService {


  private BASE: any;
  constructor(private http: HttpClient, @Inject('API_END_POINT') private API_END_POINT: any) {
    this.BASE = API_END_POINT.PLATO;
   }
  getServicesList(){
    //console.log(this.BASE.GET_SERVICES.URL);
    //console.log("http://localhost:8080/v1/services");
    //console.log(this.BASE.GET_SERVICES.URL === "http://localhost:8080/v1/services");
    return this.http.get(this.BASE.GET_SERVICES.URL);

  }
  yamlToJson(temp: string){
    return yaml.parse(temp);
  }


  addService(newService: microService): Observable<any>{
    let serviceItems = JSON.stringify(newService);
    //console.log(serviceItems);
    return this.http.post(this.BASE.ADD_SERVICES.URL, newService);

  }
}

