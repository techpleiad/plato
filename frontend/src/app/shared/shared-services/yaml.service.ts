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

  validateYAML(file: File, content: string): boolean {
    try {
      yaml.load(content);
      this.profileErrorMessage.delete(file.name);
      return true;
    }
    catch (e) {
      this.profileErrorMessage.set(file.name, e.message);
    }
    return false;
  }

  updateCssValidate(divStatus: any, file: File, content: string): void {
    if (this.validateYAML(file, content)) {
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
