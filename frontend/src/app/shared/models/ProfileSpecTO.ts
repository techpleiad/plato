export class PropertyDetail {
  constructor(private _property:string, private _owner:string){}
  get property(): string {
    return this._property;
  }
  get owner(): string {
    return this._owner;
  }

}

export class ProfileSpecTO {
    profile: string;
    yamlContent: string;
  
    jsonContent!: any;
    propertyList!: PropertyDetail[];
  
    constructor(profile: string, yamlContent: string, jsonContent: any, propertyList: PropertyDetail[]=[]) {
      this.profile = profile;
      this.yamlContent = yamlContent;
      this.jsonContent = jsonContent;
      this.propertyList = propertyList;
    }
  }
  