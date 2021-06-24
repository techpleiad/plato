import { Injectable } from '@angular/core';
import * as yaml from 'js-yaml';

@Injectable({
  providedIn: 'root'
})
export class YamlService {
  private profileErrorMessage = new Map();
  private profileYAMLLoaded = new Set();

  constructor() { }

  deleteYaml = (filename: string) => this.profileYAMLLoaded.delete(filename);
  getYaml = (filename: string) => this.profileYAMLLoaded.has(filename);
  addYaml = (filename: string) => this.profileYAMLLoaded.add(filename);
  clearYamls = () => this.profileYAMLLoaded.clear();

  replaceAll = (data: string, search: string, replace: string) => data.split(search).join(replace);

  validateYAML(content: string): boolean {
    try {
      yaml.load(content);
      //this.profileErrorMessage.delete(file.name);
      console.log("correct editing");
      return true;
    }
    catch (e) {
      console.log(e.message);
      //this.profileErrorMessage.set(e.message);
    }
    return false;
  }

  updateCssValidate(divStatus: any, content: string): void {
    if (this.validateYAML(content)) {
      this.toggleValidInValid(divStatus, 'bg-warning', 'bg-success');
    }
    else {
      this.toggleValidInValid(divStatus, 'bg-success', 'bg-warning');
    }
  }

  private toggleValidInValid(div: any, removeClass: string, addClass: string): void {
    div.classList.remove(removeClass);
    div.classList.add(addClass);
  }

  getProfileErrorMessage(file: File): any {

    const error = this.profileErrorMessage.get(file.name);
    return Boolean(error) ? error : null;
  }

  
}
