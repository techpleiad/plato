import { Component, Inject, OnInit } from '@angular/core';
import { MatDialog, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { microService } from '../microService';
import { ProfileSpecTO, PropertyDetail } from '../shared/models/ProfileSpecTO';
import { ConfigFilesService } from '../shared/shared-services/config-files.service';
import { ProfileAggregatorService } from '../shared/shared-services/profile-aggregator.service';
import { SprimeraFilesService } from '../shared/shared-services/sprimera-files.service';
import * as yaml from 'yaml';
import { CapService } from '../shared/shared-services/cap.service';
import { ResolveBranchInconsistencyService } from '../shared/shared-services/resolve-branch-inconsistency.service';
import { WarningDialogComponent } from '../shared/shared-components/warning-dialog/warning-dialog.component';
import { MatSnackBar } from '@angular/material/snack-bar';
import { customValidate } from '../customValidate';
import { RulesDataService } from '../shared/shared-services/rules-data.service';

@Component({
  selector: 'app-workspace-dialogue',
  templateUrl: './workspace-dialogue.component.html',
  styleUrls: ['./workspace-dialogue.component.css']
})
export class WorkspaceDialogueComponent implements OnInit {

  mservice:microService;
  profileList: string[] = [];
  branchList: string[] = [];

  functionList: string[] = [];
  functionValue: any;
  branchValue!: string;
  profileValue!: string;
  profileValidated = false;

  isBranchReq = false;
  isProfileReq = false;
  canProfileDefault = false;

  displayData!: string;

  // Sprimera Variables
  propertyList: PropertyDetail[]=[];
  ownerList: string[] = [];

  // Variables for consistency across branches
  isBranchConsistency = false;
  destinationBranchValue: string = "";
  sourceBranchValue: string = "";
  isBranch1Req = false;
  isBranch2Req = false;

  // Variables for consistency across profile
  isProfileConsistency = false;  
  inconsistentProfileProperties = new Map();
  inconsistentProfiles: string[] = [];
  missingProperties: any[] = [];
  ICP: string = "";
  chosenMissingProperty: string = "";

  // Consistency variables
  tempSourceData!: string;
  sourceData!: string;
  MRDocuments: any[] = [];
  keepChanges = false;
  discardChanges = false;
  sendMR = false;

  visibleProgressSpinner = false;
  showBtn = false;
  reqValidation = true; // No error msg
  isEditable = false;
  customValidateComponent = false;

  cusVal!: customValidate;
  displayedColumns = ['position', 'parentProperty', 'property', 'errorMsg'];
  dataSource: any[] = [];
  result: any;
  showTable: number = 2;
  
  constructor(private dialogRef: MatDialogRef<WorkspaceDialogueComponent>, @Inject(MAT_DIALOG_DATA) public data: microService,@Inject('WARNING_DIALOG_PARAM') private WARNING_DIALOG_PARAM: any,
  private _configFiles: ConfigFilesService, 
  private _sprimeraFilesService: SprimeraFilesService, private _profileAggregatorService: ProfileAggregatorService,
  private _capService: CapService, private _resolveBranchInconsistency: ResolveBranchInconsistencyService,
  public dialog: MatDialog, private _snackBar: MatSnackBar, private _rulesDataService: RulesDataService) {


    this.functionList = ["show individual file","sprimera",
    "consistency across branch","consistency across profile","custom validation"];
    this.mservice = data;
    this.profileList = this.mservice.profiles.map((x: any) => x.name);
    this.branchList = this.mservice.branches.map((x:any) => x.name);
    dialogRef.disableClose = true;

    this.cusVal = new customValidate();
   }

  ngOnInit(): void {}

  setFunction(functionValue: any){
    this.branchValue = "";
    this.profileValue = "";
    this.displayData = "";
    this.propertyList = [];
    this.ownerList = [];
    this.functionValue = functionValue;
    this.profileValidated = false;
    this.showBtn = true;

    this.isBranchReq = false;
    this.isProfileReq = false;
    this.canProfileDefault = false;

    this.destinationBranchValue = "";
    this.sourceBranchValue = "";
    this.sourceData = "";
    this.isBranch1Req = false;
    this.isBranch2Req = false;
    this.tempSourceData = "";
    this.sourceData = "" ;
    this.MRDocuments = [];
    this.keepChanges = false;
    this.discardChanges = false;
    this.sendMR = false;

    this.isBranchConsistency = false;
    this.isProfileConsistency = false;
    
    this.missingProperties = [];
    this.reqValidation = true;
    this.isEditable = false;
    this.customValidateComponent = false;

    this.showTable = 2;
    this.setBranchProfileReq();
  }
  setBranchProfileReq(){
    if(this.functionValue==="show merged file" || this.functionValue==="show individual file" || this.functionValue=="sprimera"){
      this.isBranchReq = true;
      this.isProfileReq = true;
    }
    if(this.functionValue==="consistency across branch"){
      this.isBranch1Req = true;
      this.isBranch2Req = true;
      this.isProfileReq = true;
    }
    if(this.functionValue==="show individual file" || this.functionValue==="consistency across branch"){
      this.canProfileDefault = true;
    }
    if(this.functionValue==="consistency across profile"){
      this.isBranchReq = true;
      this.branchValue = ""; 
    }
    if(this.functionValue==="custom validation"){
      this.isBranchReq = true;
      this.isProfileReq = true;
    }
  }
  setBranch(branchValue: any){
    if(this.keepChanges===true || this.MRDocuments.length>0){
      const dialogRef = this.dialog.open(WarningDialogComponent,this.WARNING_DIALOG_PARAM);
      
      dialogRef.afterClosed().subscribe(result=>{
        if(result==="yes"){
          this.branchValue = branchValue;
          this.ICP = "";
          this.chosenMissingProperty = "";
          this.MRDocuments = [];
          this.sendMR = false

          this.discardChangesClicked();
          this.showInconsistentProfiles();
          this.visibleProgressSpinner = true; 
        }
        else{
          console.log("Branch should not be changed");
        }
      });
    }
    else{
      this.branchValue = branchValue;
      if(this.functionValue === "consistency across profile"){
        this.showInconsistentProfiles();
        this.visibleProgressSpinner = true;
      }
    }
    
  }

  setDestinationBranch(branchValue: any){
    if(this.keepChanges===true || this.MRDocuments.length>0){
      const dialogRef = this.dialog.open(WarningDialogComponent,this.WARNING_DIALOG_PARAM);
      
      dialogRef.afterClosed().subscribe(result=>{
        if(result==="yes"){
          this.destinationBranchValue = branchValue;
          this.MRDocuments = [];
          this.sendMR = false;
          this.discardChangesClicked();
        }
        else{
          console.log("destinationBranch should not be changed");
        }
      });
    }
    else{
      this.destinationBranchValue = branchValue;
    }
    
  }
  setSourceBranch(branchValue: any){
    if(this.keepChanges===true || this.MRDocuments.length>0){
      const dialogRef = this.dialog.open(WarningDialogComponent,this.WARNING_DIALOG_PARAM);
      
      dialogRef.afterClosed().subscribe(result=>{
        if(result==="yes"){
          this.sourceBranchValue = branchValue;
          this.MRDocuments = [];
          this.sendMR = false;
          this.discardChangesClicked();
        }
        else{
          console.log("sourceBranch should not be changed");
        }
      });
    }
    else{
      this.sourceBranchValue = branchValue;
    }

  }
  setProfile(profileValue: any){
    this.profileValidated = true;
    // profile cannot be changed directly in case of branch consistency function.
    if(this.keepChanges===true){
      const dialogRef = this.dialog.open(WarningDialogComponent,this.WARNING_DIALOG_PARAM);
      
      dialogRef.afterClosed().subscribe(result=>{
        if(result==="yes"){
          this.profileValue = profileValue;
          this.sendMR = false;
          this.discardChangesClicked();
        }
        else{
          console.log("profile should not be changed");
        }
      });
    }
    else{
      this.profileValue = profileValue;
    }
  }
  
  //////// SHOWING INCONSISTENT PROFILES ////////////////////
  showInconsistentProfiles(){
    this.missingProperties = [];
    let profileAPIObject = {
      "services": [
          this.mservice.service
      ],
      "includeSuppressed": true,
       "email": {
          "sendEmail": false,
          "recipients": [
              "temp@gmail.com"
          ]
      }
    }
    this._capService.getReport(this.branchValue,profileAPIObject).subscribe((data: any[])=>{
      let actual_content = JSON.parse(JSON.stringify(data[0]));

      // Making the list of inconsistent profiles
      for(let i=0;i<actual_content["missingProperty"].length;i++){
        let profile = (actual_content["missingProperty"][i]["document"]["profile"]);
        let missingProperties = (actual_content["missingProperty"][i]["properties"]);
        this.inconsistentProfileProperties.set(profile,missingProperties);
      }
      this.inconsistentProfiles = Array.from( this.inconsistentProfileProperties.keys() );
      this.visibleProgressSpinner = false;

      if(this.inconsistentProfiles.length>0){
        this.isProfileConsistency = true;
      }
      else{
        this.displayData = "All profiles are consistent."
      }
    },
    err=>{
      let errorMsg = (err.error.error.errorMessage);
      this.showBackendFailure(errorMsg);
    }
    );
  }
  setICP(ICP: any){
    this.chosenMissingProperty = "";
    if(this.functionValue==="consistency across profile" && this.keepChanges===true){
      const dialogRef = this.dialog.open(WarningDialogComponent,this.WARNING_DIALOG_PARAM);
      
      dialogRef.afterClosed().subscribe(result=>{
        if(result==="yes"){
          this.ICP = ICP;
          this.sendMR = false;
          this.discardChangesClicked();
          this.displayData = "";
        }
        else{
          console.log("ICP should not be changed");
        }
      });
    }
    else{
      this.ICP = ICP;
    }
  }
  
  populateMissingProperty(event: any){
    console.log(this.tempSourceData);
    if(this.tempSourceData!==""){
      this.displayData = this.tempSourceData;
    }
    let jsonDisplayData = yaml.parse(this.displayData);
    this.chosenMissingProperty = event;

    let parentList = this.chosenMissingProperty.split(".");
    let curr = jsonDisplayData;
    for(let i=0;i<parentList.length-1;i++){
      if(!curr[parentList[i]]){
        curr[parentList[i]] = {};
      }
      curr = curr[parentList[i]];
    }
    curr[parentList[parentList.length-1]] = null;
    this.displayData = JSON.stringify(jsonDisplayData,null,2);

    // Removing the missing property from the list.
    let missingPropIdx = this.missingProperties.findIndex(x=>x===this.chosenMissingProperty);
    if(missingPropIdx!==-1){
      this.missingProperties.splice(missingPropIdx,1);
    }
    this.keepChanges = true;
    this.discardChanges = true;
    this.sendMR = false;
  }
  modifyProfileData(event: any){ // For codemirror updation
    if(this.tempSourceData!==""){
      this.keepChanges = true;
      this.discardChanges = true;
      this.sendMR = false;
    }
    this.tempSourceData = event;
  }
  modifySourceData(event: any){ // For Monaco Updation
    this.tempSourceData = event;
    this.keepChanges = true;
    this.discardChanges = true;
    this.sendMR = false;
  }

  keepChangesClicked(){
    this.sourceData = this.tempSourceData;
    this.tempSourceData = "";
    this.keepChanges = false;
    this.discardChanges = false;

    let found = false;
    for(let i=0;i<this.MRDocuments.length;i++){
      if(this.isBranchConsistency || this.functionValue==="show individual file"){
        if(this.MRDocuments[i].profile===this.profileValue){
          this.MRDocuments[i].document = this.sourceData;
          found = true;
        }
      }
      else if(this.isProfileConsistency){
        this.inconsistentProfileProperties.set(this.ICP,this.missingProperties);
        this.chosenMissingProperty = "";
        if(this.MRDocuments[i].profile===this.ICP){
          this.MRDocuments[i].document = this.sourceData;
          found = true;
        }
      }
    }
    if(found===false){
      if(this.isBranchConsistency){
        this.MRDocuments.push({
          "branch": this.sourceBranchValue,
          "profile": this.profileValue,
          "document": this.sourceData
        })
      }
      else if(this.isProfileConsistency){
        this.inconsistentProfileProperties.set(this.ICP,this.missingProperties);
        this.MRDocuments.push({
          "branch": this.branchValue,
          "profile": this.ICP,
          "document": this.sourceData
        })
      }
      else if(this.functionValue==="show individual file"){
        this.MRDocuments.push({
          "branch": this.branchValue,
          "profile": this.profileValue,
          "document": this.sourceData
        })
      }
    }
    let simpleSnackBarRef = this._snackBar.open("changes saved locally");
    setTimeout(simpleSnackBarRef.dismiss.bind(simpleSnackBarRef), 3000);
    this.sendMR = true;
  }
  discardChangesClicked(){
    this.displayData = this.tempSourceData;
    this.tempSourceData = "";
    this.discardChanges = false;
    this.keepChanges = false;
    this.sourceData = "";
    if(this.MRDocuments.length>0){
      this.sendMR = true;
    }
    if(this.isBranchConsistency || this.functionValue==="show individual file")
    this.sendToCodeMirror();
    if(this.isProfileConsistency===true && this.ICP!==""){
      this.chosenMissingProperty = "";
      this.missingProperties = [];
      //when branch is changed we dont have ICP so we cannot send to codemirror.
      this.sendToCodeMirror();
    }
  }
  sendMergeRequest(){
    this.visibleProgressSpinner = true;
    let body: any;
    if(this.isBranchConsistency){
      body = {
        "service": this.mservice.service,
        "branch": this.sourceBranchValue,
        "documents": this.MRDocuments
      }
    }
    else if(this.isProfileConsistency || this.functionValue==="show individual file"){
      body = {
        "service": this.mservice.service,
        "branch": this.branchValue,
        "documents": this.MRDocuments
      }
    }
    this._resolveBranchInconsistency.sendMergeRequest(body).subscribe((data:any)=>{
      let responseList = data[0].split("\n");
      let mergeRequestMail = (responseList[2].trim()); // corresponds to email of the merge request.
      this.visibleProgressSpinner = false;
      this.MRDocuments = [];

      if(mergeRequestMail.includes("github")){
        let temp = mergeRequestMail.split("pull/new");
        let temp2 = temp[0]+"compare/"+body["branch"]+".."+temp[1];
        window.open(temp2,"_blank");
      }
      else{
        let final_url = mergeRequestMail+"&merge_request[target_branch]="+body["branch"];
        console.log(final_url);
        window.open(final_url,"_blank");
      }
      
    },
    err=>{
      let errorMsg = (err.error.error.errorMessage);
      this.showBackendFailure(errorMsg);
    }
    );
    this.sendMR = false;
  }

  processFunction(){
    this.reqValidation = true;
    if(this.isBranchReq===true && !this.branchValue){
      this.reqValidation = false;
    }
    
    if(this.isProfileReq===true && !this.profileValidated){
      this.reqValidation = false;
    }
    if(this.isBranch1Req===true && this.sourceBranchValue===""){
      this.reqValidation = false;
    }
    if(this.isBranch2Req===true && this.destinationBranchValue===""){
      this.reqValidation = false;
    }
    if(this.isProfileConsistency===true && this.ICP===""){
      this.reqValidation = false;
    }
    if(this.reqValidation === true){
      this.sendToCodeMirror();
    } 
  }

  sendToCodeMirror(){
    this.visibleProgressSpinner = true;
    this.tempSourceData = "";
    this.displayData = "";

    // SHOW MERGED AND INDIVIDUAL FILES
    if(this.functionValue==="show individual file"){
      let found = false;
      for(let i=0;i<this.MRDocuments.length;i++){
          
        if(this.MRDocuments[i].profile===this.profileValue){
            this.displayData = this.MRDocuments[i].document;
            found = true;
            this.visibleProgressSpinner = false;
            this.isEditable = true;
        }
      }
      if(found===false){
        this._configFiles.getFile(this.mservice.service,"individual",this.branchValue,this.profileValue)
        .subscribe(data => {
          this.isEditable = true;
          this.visibleProgressSpinner = false;
          this.displayData = data;
        },
        err=>{
          let errorMsg = (err.error.error.errorMessage);
          this.showBackendFailure(errorMsg);
        }
        );
      }

    }
 
    // SPIMERA
    else if(this.functionValue==="sprimera"){
      let profileSpecTOList: ProfileSpecTO[] = [];
      this._sprimeraFilesService.getFiles(this.mservice.service,this.branchValue,this.profileValue).subscribe((data: any[]) =>{
        //Converting the fetched files into the format required by profile_aggregator service.
        for(let i=0;i<data.length;i++){
          profileSpecTOList.push(new ProfileSpecTO(
            `${data[i].service}-${data[i].profile}`,
            data[i].yaml,
            data[i].jsonNode,
          ))
        }

        const aggregated = this._profileAggregatorService.aggregateProfiles(profileSpecTOList);
        if(aggregated){
          this.visibleProgressSpinner = false;
        }
        this.displayData = JSON.stringify(aggregated.jsonContent,null,2);
        this.propertyList = aggregated.propertyList;
        this.ownerList = profileSpecTOList.map(function(val){
          return val.profile;
        })
      },
      err=>{
        let errorMsg = (err.error.error.errorMessage);
        this.showBackendFailure(errorMsg);
      }
      )
    }

    // CONSISTENCY ACROSS BRANCHES
    else if(this.functionValue==="consistency across branch"){

      this.isBranchConsistency = true;
      this._configFiles.getFile(this.mservice.service,this.functionValue, this.destinationBranchValue,this.profileValue)
      .subscribe(data => {
        console.log(data);
        // checkiong if the source data for this profile has some local changes.
        let found = false;
        for(let i=0;i<this.MRDocuments.length;i++){
          
          if(this.MRDocuments[i].profile===this.profileValue){
            this.sourceData = this.MRDocuments[i].document;
            found = true;
            this.visibleProgressSpinner = false;
            if(data===this.sourceData){
              this.showConsistentBranchMessage();
            }
          }
        }
        if(found===false){
          this._configFiles.getFile(this.mservice.service,this.functionValue, this.sourceBranchValue,this.profileValue)
          .subscribe(data2 => {  
            this.visibleProgressSpinner = false;
            this.sourceData = data2;
            if(data===this.sourceData){
              this.showConsistentBranchMessage();
            }
          },
          err=>{
            let errorMsg = (err.error.error.errorMessage);
            this.showBackendFailure(errorMsg);
          }
          );
        }
        
        this.displayData = data;
      },
      err=>{
        let errorMsg = (err.error.error.errorMessage);
        this.showBackendFailure(errorMsg);
      }
      ); 
    }
    //// CONSISTENCY ACROSS PROFILES
    else if(this.functionValue==="consistency across profile"){
      let found = false;
      for(let i=0;i<this.MRDocuments.length;i++){
          
        if(this.MRDocuments[i].profile===this.ICP){
            this.displayData = this.MRDocuments[i].document;
            found = true;
            this.visibleProgressSpinner = false;
        }
      }
      if(found===false){
        this._configFiles.getFile(this.mservice.service,"individual",this.branchValue,this.ICP)
        .subscribe(data => {
          this.isEditable = true;
          this.visibleProgressSpinner = false;
          this.displayData = data;
          
        },
        err=>{
          let errorMsg = (err.error.error.errorMessage);
          this.showBackendFailure(errorMsg);
        }
        );
      }
      this.missingProperties = JSON.parse(JSON.stringify(this.inconsistentProfileProperties.get(this.ICP)));
      if(this.missingProperties.length===0){
        this.showConsistentProfileMessage();
      }
    }

    else if(this.functionValue==="custom validation"){
      //this.customValidateComponent = false;
      this.customValidateComponent = true;
      console.log("customValidation");

      this.cusVal.services=[];
      this.cusVal.branches=[];
      this.cusVal.profiles=[];

      this.cusVal.services.push(this.mservice.service);
      this.cusVal.branches.push(this.branchValue);
      this.cusVal.profiles.push(this.profileValue);
      this.cusVal.email = {sendEmail: false, recipients: []};

    
      this._rulesDataService.sendCustomValidateEmail(this.cusVal).subscribe(data=>{
        this.result = JSON.parse(JSON.stringify(data));
        this.showTable = this.result[0].customValidateReportList.length;
        this.showTable = Math.min(1,this.showTable);
        
        if(this.showTable){
          let parentProperty: string = this.result[0].customValidateReportList[0].property;
          let strList: string[] = this.result[0].customValidateReportList[0].validationMessages;
          let tempData: any[] = [];
          for(let i=0;i<strList.length;i++){
            let str: string = strList[i].slice(2);
            let idx = -1;
            for(let j=0;j<str.length;j++){
              if(str[j]===':'){
                idx = j;
                break;
              }
            }
            console.log("here");
            
            tempData.push({position: i+1, parentProperty: parentProperty, property: str.slice(0,idx), errorMsg: str.slice(idx+2)});
          }
          this.dataSource = tempData;
            
        }
        this.visibleProgressSpinner = false;
        console.log(this.dataSource);
      });
    }
  }

  closeWorkspace(){
    this.dialogRef.close(WorkspaceDialogueComponent);
  }
  showBackendFailure(data:any){
    let simpleSnackBarRef = this._snackBar.open(data || "Internal error occurred","Close");
    setTimeout(simpleSnackBarRef.dismiss.bind(simpleSnackBarRef), 5000);
    this.visibleProgressSpinner = false;
  }
  showConsistentProfileMessage(){
    let simpleSnackBarRef = this._snackBar.open(`${this.ICP} profile is consistent`,"Close");
    setTimeout(simpleSnackBarRef.dismiss.bind(simpleSnackBarRef), 5000);
    this.visibleProgressSpinner = false;
  }
  showConsistentBranchMessage(){
    let simpleSnackBarRef = this._snackBar.open(`These branches are consistent`,"Close");
    setTimeout(simpleSnackBarRef.dismiss.bind(simpleSnackBarRef), 5000);
    this.visibleProgressSpinner = false;
  }
}

