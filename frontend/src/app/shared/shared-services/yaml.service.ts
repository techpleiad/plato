import { Injectable } from '@angular/core';
import * as yaml from 'js-yaml';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class YamlService {
  private profileErrorMessage = new Map();
  private profileYAMLLoaded = new Set();
  private _errorObservable$ = new BehaviorSubject<boolean>(false);

  constructor() { }

  deleteYaml = (filename: string) => this.profileYAMLLoaded.delete(filename);
  getYaml = (filename: string) => this.profileYAMLLoaded.has(filename);
  addYaml = (filename: string) => this.profileYAMLLoaded.add(filename);
  clearYamls = () => this.profileYAMLLoaded.clear();

  replaceAll = (data: string, search: string, replace: string) => data.split(search).join(replace);

  validateYAML(content: string): void{
    try {
      yaml.load(content);
      this._errorObservable$.next(false);
    }
    catch (e) {
      this._errorObservable$.next(true);
      console.log(e.message);
      //this.profileErrorMessage.set(e.message);
    }
  }
  validateJSON(content: string): void{
    try{
      JSON.parse(content);
      this._errorObservable$.next(false);
    }
    catch(e){
      console.log(e);
      this._errorObservable$.next(true);
    }
  }
  resetError(){
    this._errorObservable$.next(false);
  }
  get errorObservable$(): any{
    return this._errorObservable$;
  }
  /*
  updateCssValidate(divStatus: any, content: string): void {
    if (this.validateYAML(content)) {
      this.toggleValidInValid(divStatus, 'bg-warning', 'bg-success');
    }
    else {
      this.toggleValidInValid(divStatus, 'bg-success', 'bg-warning');
    }
  }*/

  private toggleValidInValid(div: any, removeClass: string, addClass: string): void {
    div.classList.remove(removeClass);
    div.classList.add(addClass);
  }

  getProfileErrorMessage(file: File): any {

    const error = this.profileErrorMessage.get(file.name);
    return Boolean(error) ? error : null;
  }

  
}
