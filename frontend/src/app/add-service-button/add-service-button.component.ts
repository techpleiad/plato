import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { AddNewServiceComponent } from '../add-new-service/add-new-service.component';
import { AddServiceService } from '../add-service.service';
import {microService} from '../microService';
@Component({
  selector: 'app-add-service-button',
  templateUrl: './add-service-button.component.html',
  styleUrls: ['./add-service-button.component.css']
})
export class AddServiceButtonComponent implements OnInit {

  constructor(private _addService: AddServiceService, public dialog: MatDialog) { }

  ngOnInit(): void {
  }

  openAddServiceDialog(){
    const dialogRef = this.dialog.open(AddNewServiceComponent,{
      disableClose:true,
      width: "500px",
      minHeight: "300px"
    
    });

    dialogRef.afterClosed().subscribe((result: microService) => {
      console.log(result);
      if(result){
        this.addNewService(result);
      }
      else{
        console.log("null value");
      }
    });
  }

  addNewService(newService: microService){
    this._addService.addNewService(newService);
  }

}
