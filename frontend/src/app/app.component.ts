import { Component } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { AddNewServiceComponent } from './add-new-service/add-new-service.component';
import { microService } from './microService';
import { DataManagerService } from './shared/shared-services/data-manager.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'frontend';
  constructor(private _dataManagerService: DataManagerService, public dialog: MatDialog){

  }
  openAddServiceDialog(){
    const dialogRef = this.dialog.open(AddNewServiceComponent,{
      //disableClose:true,
      width: "600px",
      minHeight: "300px"
    
    });

    dialogRef.afterClosed().subscribe((result: microService) => {
      console.log(result);
      if(result){
        //this._dataManagerService.addService(result);
        console.log(result);
      }
      else{
        console.log("null value");
      }
    });
  }
}
