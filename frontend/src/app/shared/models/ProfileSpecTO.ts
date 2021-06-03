export class ProfileSpecTO {
    profile: string;
    yamlContent: string;
  
    jsonContent!: any;
    propertyList!: any[];
  
    constructor(profile: string, yamlContent: string, jsonContent: any, propertyList: any[]=[]) {
      this.profile = profile;
      this.yamlContent = yamlContent;
      this.jsonContent = jsonContent;
      
    }
  }
  